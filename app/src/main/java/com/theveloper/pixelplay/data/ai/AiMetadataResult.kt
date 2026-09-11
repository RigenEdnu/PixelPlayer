package com.theveloper.pixelplay.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class AiMetadataResult(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val genre: String? = null
)
