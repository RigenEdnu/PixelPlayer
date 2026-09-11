package com.theveloper.pixelplay.data.backup

import com.theveloper.pixelplay.data.database.FavoritesEntity
import com.theveloper.pixelplay.data.database.LyricsEntity
import com.theveloper.pixelplay.data.database.SearchHistoryEntity
import com.theveloper.pixelplay.data.database.SongEngagementEntity
import com.theveloper.pixelplay.data.database.TransitionRuleEntity
import com.theveloper.pixelplay.data.preferences.PreferenceBackupEntry

enum class BackupSection(
    val key: String,
    val label: String,
    val description: String
) {
    PLAYLISTS(
        key = "playlists",
        label = "Playlists",
        description = "Your custom playlists and ordering preferences."
    ),
    GLOBAL_SETTINGS(
        key = "global_settings",
        label = "Global Settings",
        description = "Themes, behavior, playback, and app preferences."
    ),
    FAVORITES(
        key = "favorites",
        label = "Favorites",
        description = "Songs marked as favorite."
    ),
    LYRICS(
        key = "lyrics",
        label = "Saved Lyrics",
        description = "Lyrics you've saved or imported."
    ),
    SEARCH_HISTORY(
        key = "search_history",
        label = "Search History",
        description = "Recent search terms in the app."
    ),
    TRANSITIONS(
        key = "transitions",
        label = "Transition Rules",
        description = "Custom transition settings between songs."
    ),
    ENGAGEMENT_STATS(
        key = "engagement_stats",
        label = "Engagement Stats",
        description = "Play count and listening duration per song."
    ),
    PLAYBACK_HISTORY(
        key = "playback_history",
        label = "Playback History",
        description = "Timeline-based listening history for stats."
    );

    companion object {
        val defaultSelection: Set<BackupSection> = entries.toSet()
    }
}

enum class BackupOperationType {
    EXPORT,
    IMPORT
}

data class BackupTransferProgressUpdate(
    val operation: BackupOperationType,
    val step: Int,
    val totalSteps: Int,
    val title: String,
    val detail: String,
    val section: BackupSection? = null
) {
    val progress: Float
        get() = if (totalSteps > 0) (step.toFloat() / totalSteps).coerceIn(0f, 1f) else 0f
}

data class PlaybackHistoryBackupEntry(
    val songId: String,
    val timestamp: Long,
    val durationMs: Long,
    val startTimestamp: Long? = null,
    val endTimestamp: Long? = null
)

data class AppDataBackupPayload(
    val formatVersion: Int = 2,
    val exportedAtEpochMs: Long = System.currentTimeMillis(),
    val availableSections: Set<String> = emptySet(),
    val globalSettings: List<PreferenceBackupEntry>? = null,
    val playlists: List<PreferenceBackupEntry>? = null,
    // Legacy field from JSON v1 backups
    val preferences: List<PreferenceBackupEntry>? = null,
    val favorites: List<FavoritesEntity>? = null,
    val lyrics: List<LyricsEntity>? = null,
    val searchHistory: List<SearchHistoryEntity>? = null,
    val transitions: List<TransitionRuleEntity>? = null,
    val engagementStats: List<SongEngagementEntity>? = null,
    val playbackHistory: List<PlaybackHistoryBackupEntry>? = null
)


