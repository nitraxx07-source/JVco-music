package ca.ilianokokoro.umihi.music.ui.screens.search

import ca.ilianokokoro.umihi.music.models.Song
import ca.ilianokokoro.umihi.music.models.PlaylistInfo


data class SearchState(
    val search: String = String(),
    val screenState: ScreenState = ScreenState.Success(),
    val isLoggedIn: Boolean = false,
    val downloadingSongIds: Set<String> = emptySet(),
    val downloadProgress: Map<String, Int> = emptyMap()
)


sealed class ScreenState {
    data class Success(
        val results: List<Song> = listOf(),
        val playlists: List<PlaylistInfo> = listOf()
    ) : ScreenState()

    data object Loading : ScreenState()
    data class Error(val exception: Exception) : ScreenState()
}