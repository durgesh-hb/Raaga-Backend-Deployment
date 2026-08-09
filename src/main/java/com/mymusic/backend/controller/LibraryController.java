package com.mymusic.backend.controller;

import com.mymusic.backend.model.LikedSongEntity;
import com.mymusic.backend.model.PlaylistEntity;
import com.mymusic.backend.model.PlaylistTrackEntity;
import com.mymusic.backend.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService libraryService;

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/liked-songs")
    public ResponseEntity<List<LikedSongEntity>> getLikedSongs(@RequestParam(defaultValue = "user_default") String userId) {
        return ResponseEntity.ok(libraryService.getLikedSongs(userId));
    }

    @PostMapping("/liked-songs")
    public ResponseEntity<LikedSongEntity> toggleLikedSong(@RequestBody LikedSongEntity song) {
        if (song.getUserId() == null || song.getUserId().isBlank()) {
            song.setUserId("user_default");
        }
        LikedSongEntity result = libraryService.toggleLikedSong(song);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/liked-songs/{trackId}")
    public ResponseEntity<Void> deleteLikedSong(
            @PathVariable String trackId,
            @RequestParam(defaultValue = "user_default") String userId) {
        libraryService.deleteLikedSong(userId, trackId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/playlists")
    public ResponseEntity<List<PlaylistEntity>> getPlaylists(@RequestParam(defaultValue = "user_default") String userId) {
        return ResponseEntity.ok(libraryService.getPlaylists(userId));
    }

    @PostMapping("/playlists")
    public ResponseEntity<PlaylistEntity> createPlaylist(@RequestBody PlaylistEntity playlist) {
        if (playlist.getUserId() == null || playlist.getUserId().isBlank()) {
            playlist.setUserId("user_default");
        }
        return ResponseEntity.ok(libraryService.createPlaylist(playlist));
    }

    @DeleteMapping("/playlists/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable String playlistId,
            @RequestParam(defaultValue = "user_default") String userId) {
        libraryService.deletePlaylist(playlistId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/playlists/{playlistId}/tracks")
    public ResponseEntity<List<PlaylistTrackEntity>> getPlaylistTracks(@PathVariable String playlistId) {
        return ResponseEntity.ok(libraryService.getPlaylistTracks(playlistId));
    }

    @PostMapping("/playlists/{playlistId}/tracks")
    public ResponseEntity<PlaylistTrackEntity> addTrackToPlaylist(
            @PathVariable String playlistId,
            @RequestBody PlaylistTrackEntity track) {
        track.setPlaylistId(playlistId);
        return ResponseEntity.ok(libraryService.addTrackToPlaylist(track));
    }

    @DeleteMapping("/playlists/{playlistId}/tracks/{trackId}")
    public ResponseEntity<Void> removeTrackFromPlaylist(
            @PathVariable String playlistId,
            @PathVariable String trackId) {
        libraryService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.ok().build();
    }
}

