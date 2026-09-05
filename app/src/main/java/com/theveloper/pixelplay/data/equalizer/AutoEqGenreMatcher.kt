package com.theveloper.pixelplay.data.equalizer

/**
 * Maps audio genres to built-in EqualizerPreset presets.
 */
object AutoEqGenreMatcher {

    /**
     * Resolves an equalizer preset based on a song's genre string.
     * Falls back to FLAT if no specific genre matches.
     */
    fun matchGenreToPreset(rawGenre: String?): EqualizerPreset {
        if (rawGenre.isNullOrBlank()) return EqualizerPreset.FLAT

        val normalized = rawGenre.lowercase().trim()

        return when {
            // Rock / Metal
            normalized.contains("rock") ||
                normalized.contains("metal") ||
                normalized.contains("punk") ||
                normalized.contains("grunge") ||
                normalized.contains("alternative") -> EqualizerPreset.ROCK

            // Hip Hop / Rap / R&B
            normalized.contains("hip hop") ||
                normalized.contains("hip-hop") ||
                normalized.contains("hiphop") ||
                normalized.contains("rap") ||
                normalized.contains("trap") ||
                normalized.contains("r&b") ||
                normalized.contains("rnb") -> EqualizerPreset.HIP_HOP

            // Pop / Dance Pop
            normalized.contains("pop") ||
                normalized.contains("k-pop") ||
                normalized.contains("j-pop") ||
                normalized.contains("indie pop") -> EqualizerPreset.POP

            // Electronic / EDM / Dance
            normalized.contains("electronic") ||
                normalized.contains("edm") ||
                normalized.contains("house") ||
                normalized.contains("techno") ||
                normalized.contains("dance") ||
                normalized.contains("trance") ||
                normalized.contains("dubstep") ||
                normalized.contains("synth") -> EqualizerPreset.ELECTRONIC

            // Jazz / Blues
            normalized.contains("jazz") ||
                normalized.contains("blues") ||
                normalized.contains("soul") ||
                normalized.contains("funk") ||
                normalized.contains("swing") -> EqualizerPreset.JAZZ

            // Classical / Instrumental / Acoustic
            normalized.contains("classical") ||
                normalized.contains("orchestra") ||
                normalized.contains("symphony") ||
                normalized.contains("piano") ||
                normalized.contains("acoustic") ||
                normalized.contains("instrumental") ||
                normalized.contains("ambient") -> EqualizerPreset.CLASSICAL

            // Default fallback
            else -> EqualizerPreset.FLAT
        }
    }
}
