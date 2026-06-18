package com.pauls.lorebound.domain.ai

import android.util.Log
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini AI Provider implementation.
 * Uses the Gemini REST API directly via HTTP to avoid heavy SDK dependencies.
 */
@Singleton
class GeminiProvider @Inject constructor(
    private val apiKeyStore: AiKeyStore
) : AiProvider {

    override val type = AiProviderType.GEMINI

    override val isConfigured: Boolean
        get() = apiKeyStore.getGeminiApiKey() != null

    override suspend fun testConnection(): AiConnectionResult {
        val apiKey = apiKeyStore.getGeminiApiKey()
            ?: return AiConnectionResult(false, "No API key configured")

        return try {
            val startTime = System.currentTimeMillis()
            val url = java.net.URL(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$apiKey"
            )
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 15000

            val requestBody = """
                {
                    "contents": [{
                        "parts": [{"text": "Respond with exactly: {\"status\":\"ok\"}"}]
                    }],
                    "generationConfig": {
                        "temperature": 0.0,
                        "maxOutputTokens": 50
                    }
                }
            """.trimIndent()

            connection.outputStream.use { it.write(requestBody.toByteArray()) }

            val responseCode = connection.responseCode
            val latency = System.currentTimeMillis() - startTime

            if (responseCode == 200) {
                AiConnectionResult(true, "Connected to Gemini (${latency}ms)", latency)
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                AiConnectionResult(false, "HTTP $responseCode: $errorBody", latency)
            }
        } catch (e: Exception) {
            Log.e("GeminiProvider", "Connection test failed", e)
            AiConnectionResult(false, "Connection failed: ${e.message}")
        }
    }

    override suspend fun generateChronicleJson(prompt: String): AiGenerationResult {
        val apiKey = apiKeyStore.getGeminiApiKey()
            ?: return AiGenerationResult.Error("No API key configured")

        return try {
            val url = java.net.URL(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$apiKey"
            )
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 60000

            val escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")

            val requestBody = """
                {
                    "contents": [{
                        "parts": [{"text": "$escapedPrompt"}]
                    }],
                    "generationConfig": {
                        "temperature": 0.7,
                        "maxOutputTokens": 4096,
                        "responseMimeType": "application/json"
                    }
                }
            """.trimIndent()

            connection.outputStream.use { it.write(requestBody.toByteArray()) }

            val responseCode = connection.responseCode

            if (responseCode == 200) {
                val responseBody = connection.inputStream.bufferedReader().readText()
                val extractedJson = extractJsonFromResponse(responseBody)
                if (extractedJson != null) {
                    AiGenerationResult.Success(extractedJson)
                } else {
                    AiGenerationResult.Error("Failed to extract JSON from response")
                }
            } else {
                val errorBody = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                AiGenerationResult.Error("HTTP $responseCode: $errorBody")
            }
        } catch (e: Exception) {
            Log.e("GeminiProvider", "Generation failed", e)
            AiGenerationResult.Error("Generation failed: ${e.message}", e)
        }
    }

    private fun extractJsonFromResponse(response: String): String? {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(response)
            val candidates = root.jsonObject["candidates"]?.jsonArray
            val content = candidates?.firstOrNull()?.jsonObject?.get("content")?.jsonObject
            val parts = content?.get("parts")?.jsonArray
            val text = parts?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
            text
        } catch (e: Exception) {
            Log.e("GeminiProvider", "JSON extraction failed", e)
            null
        }
    }
}

private val kotlinx.serialization.json.JsonElement.jsonObject
    get() = this as kotlinx.serialization.json.JsonObject
private val kotlinx.serialization.json.JsonElement.jsonArray
    get() = this as kotlinx.serialization.json.JsonArray
private val kotlinx.serialization.json.JsonElement.jsonPrimitive
    get() = this as kotlinx.serialization.json.JsonPrimitive


