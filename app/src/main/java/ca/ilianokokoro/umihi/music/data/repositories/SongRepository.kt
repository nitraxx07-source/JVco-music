package ca.ilianokokoro.umihi.music.data.repositories

import ca.ilianokokoro.umihi.music.core.ApiResult
import ca.ilianokokoro.umihi.music.core.youtube.YoutubeApiClient
import ca.ilianokokoro.umihi.music.core.youtube.YoutubeDataExtractor
import ca.ilianokokoro.umihi.music.data.datasources.SongDataSource
import ca.ilianokokoro.umihi.music.extensions.toException
import ca.ilianokokoro.umihi.music.models.Song
import ca.ilianokokoro.umihi.music.models.PlaylistInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SongRepository {
    private val songDataSource = SongDataSource()

    fun search(query: String): Flow<ApiResult<List<Song>>> {
        return flow {
            emit(ApiResult.Loading)
            emit(ApiResult.Success(songDataSource.search(query)))
        }.catch { e ->
            emit(ApiResult.Error(e.toException()))
        }.flowOn(Dispatchers.IO)
    }

    fun searchAll(query: String): Flow<ApiResult<SearchResults>> {
        return flow {
            emit(ApiResult.Loading)
            val (songResponse, playlistResponse) = coroutineScope {
                val songs = async { YoutubeApiClient.search(query) }
                val playlists = async { YoutubeApiClient.searchPlaylists(query) }
                songs.await() to playlists.await()
            }
            emit(
                ApiResult.Success(
                    SearchResults(
                        songs = YoutubeDataExtractor.extractSearchResults(songResponse),
                        playlists = YoutubeDataExtractor.extractSearchPlaylists(playlistResponse)
                    )
                )
            )
        }.catch { e ->
            emit(ApiResult.Error(e.toException()))
        }.flowOn(Dispatchers.IO)
    }

    fun getSongInfo(songId: String): Flow<ApiResult<Song>> {
        return flow {
            emit(ApiResult.Loading)
            emit(ApiResult.Success(songDataSource.getSongInfo(songId)))
        }.catch { e ->
            emit(ApiResult.Error(e.toException()))
        }.flowOn(Dispatchers.IO)
    }
}

data class SearchResults(
    val songs: List<Song>,
    val playlists: List<PlaylistInfo>
)