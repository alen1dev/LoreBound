package com.pauls.lorebound.domain.ai

/**
 * Supported AI provider types.
 */
enum class AiProviderType {
    DISABLED,
    GEMINI
}

/**
 * Abstraction over AI providers.
 * All AI-powered features must go through this interface.
 */
interface AiProvider {
    val type: AiProviderType
    val isConfigured: Boolean
    suspend fun testConnection(): AiConnectionResult
    suspend fun generateChronicleJson(prompt: String): AiGenerationResult
}

data class AiConnectionResult(
    val success: Boolean,
    val message: String,
    val latencyMs: Long = 0L
)

sealed interface AiGenerationResult {
    data class Success(val json: String) : AiGenerationResult
    data class Error(val message: String, val cause: Throwable? = null) : AiGenerationResult
}

