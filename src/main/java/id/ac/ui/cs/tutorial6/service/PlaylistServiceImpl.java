package id.ac.ui.cs.tutorial6.service;

import id.ac.ui.cs.tutorial6.model.playlist.Playlist;
import id.ac.ui.cs.tutorial6.model.song.Song;
import id.ac.ui.cs.tutorial6.repository.PlaylistRepository;
import id.ac.ui.cs.tutorial6.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PlaylistServiceImpl implements PlaylistService {

  @Autowired
  PlaylistRepository playlistRepository;

  @Autowired
  SongRepository songRepository;

  @Override
  public Playlist createPlaylist(String name) {
    Playlist playlist = Playlist.builder()
                        .name(name)
                        .build();
    return playlistRepository.save(playlist);
  }

  @Override
  public List<Playlist> getPlaylists() {
    return playlistRepository.findAll();
  }

  @Override
  public Playlist getPlaylistById(String id) {
    return playlistRepository.getById(id);
  }

  @Override
  public Song getSongInPlaylist(Playlist playlist, String songId) {
    for (Song song: playlist.getSongsSet()) {
      if (song.getId().equals(songId)) {
        return song;
      }
    }
    return null;
  }

  @Override
  public Playlist addSongsToPlaylist(String idPlaylist, String[] idSongs) {
    return modifySongAtPlaylist(idPlaylist, idSongs, "add");
  }

  @Override
  public Playlist deleteSongFromPlaylist(String idPlaylist, String[] idSongs) {
    return modifySongAtPlaylist(idPlaylist, idSongs, "remove");
  }

  public Playlist modifySongAtPlaylist(String idPlaylist, String[] idSongs, String activity) {
    Set<Song> songsSet = playlistRepository.getById(idPlaylist).getSongsSet();
    for (String id : idSongs) {
      Song song = songRepository.findById(id).get();
      if (activity.equals("add")) {
        songsSet.add(song);
      } else if (activity.equals("remove")) {
        songsSet.remove(song);
      }
    }
    Playlist playlist = getPlaylistById(idPlaylist);
    playlist.setSongsSet(songsSet);
    return playlistRepository.save(playlist);
  }

  @Override
  public Playlist updatePlaylist(String id, Optional<String> nameOpt, Optional<Set<Song>> songsSetOpt) {
    Playlist playlist = getPlaylistById(id);
    nameOpt.ifPresent(playlist::setName);
    songsSetOpt.ifPresent(playlist::setSongsSet);
    return playlistRepository.save(playlist);
  }

  @Override
  public void deletePlaylist(String id) {
    playlistRepository.deleteById(id);
  }
}
