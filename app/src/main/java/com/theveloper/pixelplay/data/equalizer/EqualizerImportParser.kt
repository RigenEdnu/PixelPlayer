package com.theveloper.pixelplay.data.equalizer

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.log10
import kotlin.math.roundToInt

object EqualizerImportParser {

    // Target 10-band ISO standard frequencies in Hz
    val TARGET_FREQUENCIES = listOf(31.0, 62.0, 125.0, 250.0, 500.0, 1000.0, 2000.0, 4000.0, 8000.0, 16000.0)

    private val jsonParser = Json { ignoreUnknownKeys = true }

    /**
     * Parses an equalizer preset from an imported file URI.
     * Supports GraphicEQ (Wavelet/AutoEq) text files and native JSON preset files.
     */
    fun parsePresetFromUri(contentResolver: ContentResolver, uri: Uri): Result<EqualizerPreset> {
        return runCatching {
            val fileName = getFileName(contentResolver, uri) ?: "Imported Preset"
            val baseName = fileName.substringBeforeLast(".")

            val content = contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: throw IllegalArgumentException("Could not read file from storage")

            parsePresetContent(baseName, content)
        }
    }

    /**
     * Parses raw string content as either JSON or GraphicEQ format.
     */
    fun parsePresetContent(presetName: String, content: String): EqualizerPreset {
        val trimmed = content.trim()

        // 1. Try parsing as JSON EqualizerPreset
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            val jsonResult = runCatching {
                if (trimmed.startsWith("{")) {
                    jsonParser.decodeFromString<EqualizerPreset>(trimmed).copy(isCustom = true)
                } else {
                    val bandLevels = jsonParser.decodeFromString<List<Int>>(trimmed)
                    val normalized = normalizeBandLevels(bandLevels)
                    EqualizerPreset(
                        name = presetName,
                        displayName = presetName,
                        bandLevels = normalized,
                        isCustom = true
                    )
                }
            }
            if (jsonResult.isSuccess) {
                return jsonResult.getOrThrow()
            }
        }

        // 2. Try parsing as GraphicEQ / Wavelet format
        val graphicEqBands = parseGraphicEq(trimmed)
        if (graphicEqBands != null) {
            return EqualizerPreset(
                name = presetName,
                displayName = presetName,
                bandLevels = graphicEqBands,
                isCustom = true
            )
        }

        throw IllegalArgumentException("Unsupported equalizer file format")
    }

    /**
     * Parses GraphicEQ format: "GraphicEQ: 20 -0.9; 25 -1.2; ... 20000 1.5"
     * Interpolates to the target 10-band ISO frequencies using log-frequency interpolation.
     */
    fun parseGraphicEq(content: String): List<Int>? {
        val line = content.lines().firstOrNull { it.trim().startsWith("GraphicEQ:", ignoreCase = true) }
            ?: if (content.contains(";") && content.any { it.isDigit() }) content else null
            ?: return null

        val dataPart = line.substringAfter("GraphicEQ:", line).trim()
        val points = mutableListOf<Pair<Double, Double>>()

        val tokens = dataPart.split(";", "\n")
        for (token in tokens) {
            val parts = token.trim().split("\\s+".toRegex())
            if (parts.size >= 2) {
                val freq = parts[0].toDoubleOrNull()
                val gain = parts[1].toDoubleOrNull()
                if (freq != null && gain != null && freq > 0) {
                    points.add(freq to gain)
                }
            }
        }

        if (points.isEmpty()) return null

        // Sort by frequency
        points.sortBy { it.first }

        // Interpolate across TARGET_FREQUENCIES
        return TARGET_FREQUENCIES.map { targetFreq ->
            val gain = interpolateGain(points, targetFreq)
            gain.roundToInt().coerceIn(-15, 15)
        }
    }

    private fun interpolateGain(points: List<Pair<Double, Double>>, targetFreq: Double): Double {
        if (points.isEmpty()) return 0.0
        if (targetFreq <= points.first().first) return points.first().second
        if (targetFreq >= points.last().first) return points.last().second

        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            if (targetFreq >= p1.first && targetFreq <= p2.first) {
                if (p1.first == p2.first) return p1.second
                // Logarithmic frequency interpolation
                val logF1 = log10(p1.first)
                val logF2 = log10(p2.first)
                val logTarget = log10(targetFreq)
                val fraction = (logTarget - logF1) / (logF2 - logF1)
                return p1.second + fraction * (p2.second - p1.second)
            }
        }
        return 0.0
    }

    private fun normalizeBandLevels(bands: List<Int>): List<Int> {
        return when {
            bands.size >= 10 -> bands.take(10).map { it.coerceIn(-15, 15) }
            bands.isEmpty() -> List(10) { 0 }
            else -> (bands + List(10 - bands.size) { 0 }).map { it.coerceIn(-15, 15) }
        }
    }

    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return cursor.getString(nameIndex)
                    }
                }
            }
        }
        return uri.path?.substringAfterLast('/')
    }
}
