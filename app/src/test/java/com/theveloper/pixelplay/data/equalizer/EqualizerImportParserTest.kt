package com.theveloper.pixelplay.data.equalizer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EqualizerImportParserTest {

    @Test
    fun testParseGraphicEqWavelet() {
        val rawData = "GraphicEQ: 20 -0.9; 21 -0.9; 22 -1.0; 31 -1.8; 62 -3.2; 125 -4.0; 250 -2.3; 500 -0.1; 1000 -1.3; 2000 -2.1; 4000 -3.6; 8000 -4.8; 16000 -1.4; 19871 -1.2"
        val bands = EqualizerImportParser.parseGraphicEq(rawData)

        assertNotNull(bands)
        assertEquals(10, bands!!.size)
        // Verify clamping and values around key bands
        assertEquals(-2, bands[0]) // 31Hz -> ~ -1.8 -> -2
        assertEquals(-3, bands[1]) // 62Hz -> ~ -3.2 -> -3
        assertEquals(-4, bands[2]) // 125Hz -> ~ -4.0 -> -4
        assertEquals(-2, bands[3]) // 250Hz -> ~ -2.3 -> -2
        assertEquals(0, bands[4])  // 500Hz -> ~ -0.1 -> 0
    }

    @Test
    fun testParsePresetJson() {
        val jsonContent = """{"name":"MyPreset","displayName":"MyPreset","bandLevels":[2,3,4,5,-1,-2,0,1,2,3],"isCustom":true}"""
        val preset = EqualizerImportParser.parsePresetContent("FallbackName", jsonContent)

        assertEquals("MyPreset", preset.name)
        assertEquals(10, preset.bandLevels.size)
        assertEquals(2, preset.bandLevels[0])
        assertTrue(preset.isCustom)
    }

    @Test
    fun testParseBandLevelsArrayJson() {
        val jsonArray = "[5, 4, 3, 2, 1, 0, -1, -2, -3, -4]"
        val preset = EqualizerImportParser.parsePresetContent("ArrayPreset", jsonArray)

        assertEquals("ArrayPreset", preset.name)
        assertEquals(10, preset.bandLevels.size)
        assertEquals(5, preset.bandLevels[0])
        assertEquals(-4, preset.bandLevels[9])
    }
}
