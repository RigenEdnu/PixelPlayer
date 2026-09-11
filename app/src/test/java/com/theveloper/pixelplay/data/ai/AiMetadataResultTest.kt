package com.theveloper.pixelplay.data.ai

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class AiMetadataResultTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parseAiMetadataResult_validJson_success() {
        val rawJson = """
            {"title":"Bohemian Rhapsody","artist":"Queen","album":"A Night at the Opera","genre":"Progressive Rock"}
        """.trimIndent()

        val cleaned = AiResponseCleaner.cleanResponse(rawJson)
        val jsonObject = AiResponseCleaner.extractJsonObject(cleaned)
        assertNotNull(jsonObject)

        val result = json.decodeFromString<AiMetadataResult>(jsonObject!!)
        assertEquals("Bohemian Rhapsody", result.title)
        assertEquals("Queen", result.artist)
        assertEquals("A Night at the Opera", result.album)
        assertEquals("Progressive Rock", result.genre)
    }

    @Test
    fun parseAiMetadataResult_markdownFencedJson_success() {
        val rawResponse = """
            Here is the metadata:
            ```json
            {
              "title": "Thriller",
              "artist": "Michael Jackson",
              "album": "Thriller",
              "genre": "Pop"
            }
            ```
        """.trimIndent()

        val cleaned = AiResponseCleaner.cleanResponse(rawResponse)
        val jsonObject = AiResponseCleaner.extractJsonObject(cleaned)
        assertNotNull(jsonObject)

        val result = json.decodeFromString<AiMetadataResult>(jsonObject!!)
        assertEquals("Thriller", result.title)
        assertEquals("Michael Jackson", result.artist)
        assertEquals("Thriller", result.album)
        assertEquals("Pop", result.genre)
    }

    @Test
    fun parseAiMetadataResult_partialFields_preservesMissingAsNull() {
        val rawJson = """
            {"genre":"Synthwave"}
        """.trimIndent()

        val cleaned = AiResponseCleaner.cleanResponse(rawJson)
        val jsonObject = AiResponseCleaner.extractJsonObject(cleaned)
        assertNotNull(jsonObject)

        val result = json.decodeFromString<AiMetadataResult>(jsonObject!!)
        assertEquals(null, result.title)
        assertEquals(null, result.artist)
        assertEquals(null, result.album)
        assertEquals("Synthwave", result.genre)
    }
}
