package id.ac.ui.cs.tutorial6.controller;

import id.ac.ui.cs.tutorial6.model.musicplayer.MusicPlayer;
import id.ac.ui.cs.tutorial6.model.musicplayer.MusicPlayerState;
import id.ac.ui.cs.tutorial6.model.playlist.Playlist;
import id.ac.ui.cs.tutorial6.model.song.Song;
import id.ac.ui.cs.tutorial6.model.song.SongGenre;
import id.ac.ui.cs.tutorial6.service.MusicService;
import id.ac.ui.cs.tutorial6.service.PlaylistService;
import id.ac.ui.cs.tutorial6.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/music-player")
public class MusicPlayerController {

    // Constants of string for params
    private static final String SONGS_STRING = "songs";
    private static final String PLAYLIST_STRING = "playlist";
    private static final String REDIRECT_MUSIC_PLAYER_STRING = "redirect:/music-player";
    private static final String REDIRECT_MUSIC_PLAYER_SLASH_STRING = "redirect:/music-player/";

    @Autowired
    private MusicService musicService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private SongService songService;

    @GetMapping()
    public String homepage(Model model) {
        model.addAttribute("playlists", playlistService.getPlaylists());
        model.addAttribute(SONGS_STRING, songService.getSongs());
        return "homepage_and_detail_playlist";
    }

    @GetMapping(value = "/{playlistId}")
    public String getSongsByPlaylist(@PathVariable String playlistId,
                                     Model model) {
        var playlist = playlistService.getPlaylistById(playlistId);
        model.addAttribute("currentPlaylist", playlist);
        model.addAttribute(SONGS_STRING, playlist.getSongsSet());
        return "homepage_and_detail_playlist";
    }

    @GetMapping(value = "/{playlistId}/{songId}")
    public String playingSong(@PathVariable String playlistId,
                              @PathVariable String songId,
                              @RequestParam String state,
                              Model model) {
        var playlist = playlistService.getPlaylistById(playlistId);
        var song = playlistService.getSongInPlaylist(playlist, songId);

        musicService.setting(playlist, song);
        MusicPlayer musicPlayer = musicService.getMusicPlayer(state);
        MusicPlayerState currentState = musicPlayer.getCurrentState();

        model.addAttribute(PLAYLIST_STRING, playlist);
        model.addAttribute("song", song);
        model.addAttribute("state", currentState.toString());
        return "music_player";
    }

    @GetMapping(value = "/add-playlist")
    public String createPlaylist(Model model) {
        model.addAttribute("newPlaylist", new Playlist());
        return "form_playlist";
    }

    @PostMapping(value = "/add-playlist")
    public String createPlaylist(@RequestParam String name) {
        playlistService.createPlaylist(name);
        return REDIRECT_MUSIC_PLAYER_STRING;
    }

    @GetMapping(value = "/add-song")
    public String createSong(Model model) {
        model.addAttribute("genres", SongGenre.values());
        model.addAttribute("newSong", new Song());
        return "form_song";
    }

    @PostMapping(value = "/add-song")
    public String createSong(@RequestParam String name,
                             @RequestParam SongGenre genre) {
        songService.createSong(name, genre);
        return REDIRECT_MUSIC_PLAYER_STRING;
    }

    @GetMapping(value = "/{playlistId}/add-song")
    public String addSongToPlaylist(@PathVariable String playlistId, Model model) {
        model.addAttribute(PLAYLIST_STRING, playlistService.getPlaylistById(playlistId));
        model.addAttribute(SONGS_STRING, songService.getSongs());
        return "form_add_song_to_playlist";
    }

    @PostMapping(value = "/{playlistId}/add-song")
    public String addSongToPlaylist(@PathVariable String playlistId,
                                    @RequestParam(value="songId") String[] songId) {
        playlistService.addSongsToPlaylist(playlistId, songId);
        return REDIRECT_MUSIC_PLAYER_SLASH_STRING + playlistId;
    }

    @GetMapping(value = "/{playlistId}/delete-song")
    public String deleteSongFromPlaylist(@PathVariable String playlistId, Model model) {
        var playlist = playlistService.getPlaylistById(playlistId);
        model.addAttribute(PLAYLIST_STRING, playlist);
        model.addAttribute(SONGS_STRING, playlist.getSongsSet());
        return "form_add_song_to_playlist";
    }

    @PostMapping(value = "/{playlistId}/delete-song")
    public String deleteSongFromPlaylist(@PathVariable String playlistId,
                                    @RequestParam(value="songId") String[] songId) {
        playlistService.deleteSongFromPlaylist(playlistId, songId);
        return REDIRECT_MUSIC_PLAYER_SLASH_STRING + playlistId;
    }

    @GetMapping(value = "/playlist/{playlistId}/delete-playlist")
    public String deletePlaylist(@PathVariable String playlistId) {
      playlistService.deletePlaylist(playlistId);
      return REDIRECT_MUSIC_PLAYER_STRING;
    }

    @GetMapping(value = "/song/{songId}/delete-song")
    public String deleteSong(@PathVariable String songId) {
      songService.deleteSong(songId);
      return REDIRECT_MUSIC_PLAYER_STRING;
    }

    @PutMapping(value = "/{playlistId}")
    public String updatePlaylist(@PathVariable String playlistId,
                                 @RequestParam String name,
                                 @RequestParam(value="songId") String[] songId ) {
      playlistService.updatePlaylist(playlistId, Optional.ofNullable(name), Optional.empty());
      playlistService.addSongsToPlaylist(playlistId, songId);
      return REDIRECT_MUSIC_PLAYER_SLASH_STRING + playlistId;
    }
}
