package ca.ilianokokoro.umihi.music.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import ca.ilianokokoro.umihi.music.models.Cookies
import ca.ilianokokoro.umihi.music.models.DownloadQuality
import ca.ilianokokoro.umihi.music.models.ThemeMode
import ca.ilianokokoro.umihi.music.models.UmihiSettings
import ca.ilianokokoro.umihi.music.models.UpdateChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DatastoreRepository(private val context: Context) {
    companion object {
        typealias UpdateChannel = ca.ilianokokoro.umihi.music.models.UpdateChannel
        val PreferenceKeys = ca.ilianokokoro.umihi.music.data.repositories.PreferenceKeys
    }

    val settings: Flow<UmihiSettings> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val themeStr = prefs[PreferenceKeys.THEME_MODE] ?: "SYSTEM"
            val parsedTheme = try { ThemeMode.valueOf(themeStr.uppercase()) } catch (e: Exception) { ThemeMode.SYSTEM }

            val channelStr = prefs[PreferenceKeys.UPDATE_CHANNEL] ?: "STABLE"
            val parsedChannel = try { UpdateChannel.valueOf(channelStr.uppercase()) } catch (e: Exception) { UpdateChannel.STABLE }

            val cookies = prefs[PreferenceKeys.COOKIES] ?: ""

            UmihiSettings(
                skipSilence = prefs[PreferenceKeys.SKIP_SILENCE] ?: false,
                downloadQuality = try {
                    DownloadQuality.valueOf(
                        (prefs[PreferenceKeys.DOWNLOAD_QUALITY] ?: DownloadQuality.HIGH.name)
                            .uppercase()
                    )
                } catch (_: IllegalArgumentException) {
                    DownloadQuality.HIGH
                },
                themeMode = parsedTheme,
                useSpecialLanguage = prefs[PreferenceKeys.USE_SPECIAL_LANGUAGE] ?: false,
                useAudioOffload = prefs[PreferenceKeys.USE_AUDIO_OFFLOAD] ?: true,
                exoPlayerCacheSizeMB = prefs[PreferenceKeys.EXO_PLAYER_CACHE_SIZE_MB] ?: 512,
                keepScreenOn = prefs[PreferenceKeys.KEEP_SCREEN_ON] ?: false,
                updateChannel = parsedChannel,
                updateChecking = prefs[PreferenceKeys.UPDATE_CHECKING] ?: true,
                cookies = cookies,
                dataSyncId = prefs[PreferenceKeys.DATA_SYNC_ID] ?: "",
                sendPlaybackData = prefs[PreferenceKeys.SEND_PLAYBACK_DATA] ?: false,
                downloadOnMetered = prefs[PreferenceKeys.DOWNLOAD_ON_METERED] ?: false,
                thumbnailCacheSizeMB = prefs[PreferenceKeys.THUMBNAIL_CACHE_SIZE_MB] ?: 128,
                appVolume = prefs[PreferenceKeys.APP_VOLUME] ?: 100,
                canTrack = (prefs[PreferenceKeys.SEND_PLAYBACK_DATA] ?: false) && !cookies.isNullOrBlank()
            )
        }

    fun getSettings(): UmihiSettings = runBlocking {
        settings.first()
    }

    suspend fun updateSkipSilence(enabled: Boolean) {
        context.dataStore.edit { it[PreferenceKeys.SKIP_SILENCE] = enabled }
    }

    suspend fun updateDownloadQuality(quality: DownloadQuality) {
        context.dataStore.edit { it[PreferenceKeys.DOWNLOAD_QUALITY] = quality.name }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferenceKeys.THEME_MODE] = mode.name }
    }

    suspend fun saveDataSyncId(id: String) {
        context.dataStore.edit { it[PreferenceKeys.DATA_SYNC_ID] = id }
    }

    suspend fun saveCookies(cookies: String) {
        context.dataStore.edit { it[PreferenceKeys.COOKIES] = cookies }
    }

    suspend fun saveCookies(cookies: Cookies) {
        saveCookies(cookies.raw)
    }

    suspend fun logOut() {
        context.dataStore.edit {
            it[PreferenceKeys.COOKIES] = ""
            it[PreferenceKeys.DATA_SYNC_ID] = ""
        }
    }

    suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }
}