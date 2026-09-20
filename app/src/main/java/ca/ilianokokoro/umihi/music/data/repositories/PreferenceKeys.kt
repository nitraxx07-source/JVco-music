package ca.ilianokokoro.umihi.music.data.repositories

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val UPDATE_CHANNEL = stringPreferencesKey("update_channel")
    val SKIP_SILENCE = booleanPreferencesKey("skip_silence")
    val DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")
    val USE_SPECIAL_LANGUAGE = booleanPreferencesKey("use_special_language")
    val USE_AUDIO_OFFLOAD = booleanPreferencesKey("use_audio_offload")
    val EXO_PLAYER_CACHE_SIZE_MB = intPreferencesKey("exo_player_cache_size_mb")
    val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
    val UPDATE_CHECKING = booleanPreferencesKey("update_checking")
    val AUTO_UPDATE = UPDATE_CHECKING
    val COOKIES = stringPreferencesKey("cookies")
    val DATA_SYNC_ID = stringPreferencesKey("data_sync_id")
    val SEND_PLAYBACK_DATA = booleanPreferencesKey("send_playback_data")
    val DOWNLOAD_ON_METERED = booleanPreferencesKey("download_on_metered")
    val THUMBNAIL_CACHE_SIZE_MB = intPreferencesKey("thumbnail_cache_size_mb")
    val EXOPLAYER_CACHE_SIZE = EXO_PLAYER_CACHE_SIZE_MB
    val THUMBNAIL_CACHE_SIZE = THUMBNAIL_CACHE_SIZE_MB
    val APP_VOLUME = intPreferencesKey("app_volume")
}