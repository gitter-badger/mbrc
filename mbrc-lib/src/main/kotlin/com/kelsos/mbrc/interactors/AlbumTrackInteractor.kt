package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.domain.AlbumTrackModel
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.mappers.TrackMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import com.kelsos.mbrc.repository.library.TrackRepository
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class AlbumTrackInteractor {
    @Inject private lateinit var repository: AlbumRepository
    @Inject private lateinit var trackRepository: TrackRepository

    fun execute(id: Long): Observable<AlbumTrackModel> {
        val tracks = trackRepository.getTracksByAlbumId(id)
                .flatMap<List<Track>>(Func1 {
                    Observable.defer { Observable.just<List<Track>>(TrackMapper.map(it)) }
                })
        return Observable.zip<List<Track>, Album, AlbumTrackModel>(tracks, getAlbum(id), {
            tracks, album -> AlbumTrackModel(tracks, album)
        }).subscribeOn(Schedulers.io())
    }

    private fun getAlbum(id: Long): Observable<Album> {
        return Observable.create<Album> { subscriber ->
            val albumView = repository.getAlbumViewById(id.toInt())
            if (albumView != null) {
                val album = AlbumMapper.map(albumView, repository.getAlbumYear(id))
                subscriber.onNext(album)
            }
            subscriber.onCompleted()
        }
    }
}