package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import java.util.List;
import rx.Observable;

public interface LibraryRepository {
  Observable<AlbumDao> getAlbums();

  Observable<GenreDao> getGenres();

  Observable<TrackDao> getTracks();

  Observable<ArtistDao> getArtists();

  void saveGenres(List<GenreDao> objects);

  void saveArtists(List<ArtistDao> objects);

  void saveTracks(List<TrackDao> objects);

  void saveAlbums(List<AlbumDao> objects);

  void saveRemoteTracks(List<TrackDto> data);

  void saveRemoteAlbums(List<AlbumDto> data);
}