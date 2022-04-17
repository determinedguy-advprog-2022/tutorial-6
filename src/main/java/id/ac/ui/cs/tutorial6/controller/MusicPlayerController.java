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

    @Autowired
    private MusicService musicService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private SongService songService;

    @GetMapping()
    public String homepage(Model model) {
        model.addAttribute("playlists", playlistService.getPlaylists());
        model.addAttribute("songs", songService.getSongs());
        return "homepage_and_detail_playlist";
    }

    @GetMapping(value = "/{playlistId}")
    public String getSongsByPlaylist(@PathVariable String playlistId,
                                     Model model) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        model.addAttribute("currentPlaylist", playlist);
        model.addAttribute("songs", playlist.getSongsSet());
        return "homepage_and_detail_playlist";
    }

    @GetMapping(value = "/{playlistId}/{songId}")
    public String playingSong(@PathVariable String playlistId,
                              @PathVariable String songId,
                              @RequestParam String state,
                              Model model) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        Song song = playlistService.getSongInPlaylist(playlist, songId);

        musicService.setting(playlist, song);
        MusicPlayer musicPlayer = musicService.getMusicPlayer(state);
        MusicPlayerState currentState = musicPlayer.getCurrentState();

        model.addAttribute("playlist", playlist);
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
        return "redirect:/music-player";
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
        return "redirect:/music-player";
    }

    @GetMapping(value = "/{playlistId}/add-song")
    public String addSongToPlaylist(@PathVariable String playlistId, Model model) {
        model.addAttribute("playlist", playlistService.getPlaylistById(playlistId));
        model.addAttribute("songs", songService.getSongs());
        return "form_add_song_to_playlist";
    }

    @PostMapping(value = "/{playlistId}/add-song")
    public String addSongToPlaylist(@PathVariable String playlistId,
                                    @RequestParam(value="songId") String[] songId) {
        playlistService.addSongsToPlaylist(playlistId, songId);
        return "redirect:/music-player/" + playlistId;
    }

    @GetMapping(value = "/{playlistId}/delete-song")
    public String deleteSongFromPlaylist(@PathVariable String playlistId, Model model) {
        Playlist playlist = playlistService.getPlaylistById(playlistId);
        model.addAttribute("playlist", playlist);
        model.addAttribute("songs", playlist.getSongsSet());
        return "form_add_song_to_playlist";
    }

    @PostMapping(value = "/{playlistId}/delete-song")
    public String deleteSongFromPlaylist(@PathVariable String playlistId,
                                    @RequestParam(value="songId") String[] songId) {
        playlistService.deleteSongFromPlaylist(playlistId, songId);
        return "redirect:/music-player/" + playlistId;
    }

    @GetMapping(value = "/playlist/{playlistId}/delete-playlist")
    public String deletePlaylist(@PathVariable String playlistId) {
      playlistService.deletePlaylist(playlistId);
      return "redirect:/music-player";
    }

    @GetMapping(value = "/song/{songId}/delete-song")
    public String deleteSong(@PathVariable String songId) {
      songService.deleteSong(songId);
      return "redirect:/music-player";
    }

    @PutMapping(value = "/{playlistId}")
    public String updatePlaylist(@PathVariable String playlistId,
                                 @RequestParam String name,
                                 @RequestParam(value="songId") String[] songId ) {
      playlistService.updatePlaylist(playlistId, Optional.ofNullable(name), Optional.empty());
      playlistService.addSongsToPlaylist(playlistId, songId);
      return "redirect:/music-player/" + playlistId;
    }
}
