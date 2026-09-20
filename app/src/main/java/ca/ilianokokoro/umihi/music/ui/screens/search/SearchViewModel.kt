package ca.ilianokokoro.umihi.music.ui.screens.search


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ca.ilianokokoro.umihi.music.core.ApiResult
import ca.ilianokokoro.umihi.music.data.repositories.DatastoreRepository
import ca.ilianokokoro.umihi.music.data.repositories.DownloadRepository
import ca.ilianokokoro.umihi.music.data.repositories.SongRepository
import ca.ilianokokoro.umihi.music.data.database.AppDatabase
import ca.ilianokokoro.umihi.music.models.Playlist
import ca.ilianokokoro.umihi.music.models.PlaylistInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SearchState())
    val uiState = _uiState.asStateFlow()

    val songRepository = SongRepository()
    private val downloadRepository = DownloadRepository(application)
    private val datastoreRepository = DatastoreRepository(application)

    init {
        observeLoginState()
    }

    private fun observeLoginState() {
        viewModelScope.launch {
            datastoreRepository.settings.map { it.cookies }.collect { cookies ->
                _uiState.update { it.copy(isLoggedIn = cookies.isNotEmpty()) }
            }
        }
    }


    fun search() {
        viewModelScope.launch {
            if (_uiState.value.search.isBlank()) {
                _uiState.update {
                    _uiState.value.copy(
                        screenState =
                            ScreenState.Success(results = listOf())
                    )
                }
                return@launch
            }

            songRepository.searchAll(_uiState.value.search).collect { apiResult ->
                _uiState.update {
                    _uiState.value.copy(
                        screenState = when (apiResult) {
                            ApiResult.Loading -> ScreenState.Loading
                            is ApiResult.Error -> ScreenState.Error(apiResult.exception)
                            is ApiResult.Success -> {
                                ScreenState.Success(
                                    results = apiResult.data.songs,
                                    playlists = apiResult.data.playlists
                                )
                            }
                        }
                    )
                }
            }
        }

    }

    fun downloadSong(song: ca.ilianokokoro.umihi.music.models.Song) {
        viewModelScope.launch {
            val playlist = Playlist(
                info = PlaylistInfo(
                    id = "_search_downloads_",
                    title = "JV music"
                ),
                songs = listOf(song)
            )
            val settings = datastoreRepository.getSettings()
            downloadRepository.downloadSong(playlist, song, settings.downloadOnMetered)
            observeSongDownload(song.youtubeId)
        }
    }

    private fun observeSongDownload(songId: String) {
        viewModelScope.launch {
            downloadRepository.getSongJobFlow("_search_downloads_$songId").collect { workInfos ->
                val workInfo = workInfos.firstOrNull() ?: return@collect
                val progress = workInfo.progress.getInt("progress", -1)
                _uiState.update { state ->
                    val active = state.downloadingSongIds.toMutableSet()
                    val values = state.downloadProgress.toMutableMap()
                    if (workInfo.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                        active.remove(songId)
                        values[songId] = 100
                    } else if (workInfo.state == androidx.work.WorkInfo.State.FAILED) {
                        active.remove(songId)
                    } else {
                        active.add(songId)
                        if (progress >= 0) values[songId] = progress
                    }
                    state.copy(downloadingSongIds = active, downloadProgress = values)
                }
            }
        }
    }


    fun onSearchFieldChange(newValue: String) {
        _uiState.update {
            it.copy(search = newValue)
        }
    }


    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(application)
            }
        }
    }
}