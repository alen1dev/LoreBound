package com.pauls.lorebound.domain.ai

import android.content.Context
import com.pauls.lorebound.domain.model.Chronicle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local storage for generated Chronicle JSON files.
 * Allows developers to inspect and review AI output.
 */
@Singleton
class ChronicleStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val storageDir: File
        get() = File(context.filesDir, "chronicles").also { it.mkdirs() }

    fun saveChronicleJson(year: Int, rawJson: String) {
        File(storageDir, "chronicle_$year.json").writeText(rawJson)
    }

    fun loadChronicleJson(year: Int): String? {
        val file = File(storageDir, "chronicle_$year.json")
        return if (file.exists()) file.readText() else null
    }

    fun loadChronicle(year: Int): Chronicle? {
        val raw = loadChronicleJson(year) ?: return null
        return try {
            json.decodeFromString<Chronicle>(raw)
        } catch (_: Exception) {
            null
        }
    }

    fun deleteChronicle(year: Int) {
        File(storageDir, "chronicle_$year.json").delete()
    }

    fun deleteAll() {
        storageDir.listFiles()?.forEach { it.delete() }
    }

    fun listYears(): List<Int> {
        return storageDir.listFiles()
            ?.mapNotNull { file ->
                val match = Regex("chronicle_(\\d{4})\\.json").find(file.name)
                match?.groupValues?.get(1)?.toIntOrNull()
            }
            ?.sorted() ?: emptyList()
    }
}

