package ca.ilianokokoro.umihi.music.models

data class UmihiSettings(
    val skipSilence: Boolean = false,
    val downloadQuality: DownloadQuality = DownloadQuality.HIGH,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val useSpecialLanguage: Boolean = false,
    val useAudioOffload: Boolean = true,
    val exoPlayerCacheSizeMB: Int = 512,
    val keepScreenOn: Boolean = false,
    val updateChannel: UpdateChannel = UpdateChannel.STABLE,
    val updateChecking: Boolean = true,
    val cookies: String = "",
    val dataSyncId: String = "",
    val sendPlaybackData: Boolean = false,
    val canTrack: Boolean = sendPlaybackData && cookies.isNotEmpty(),
    val downloadOnMetered: Boolean = false,
    val thumbnailCacheSizeMB: Int = 128,
    val appVolume: Int = 100
)
