package com.mymusic.backend.service;

import com.mymusic.backend.model.LikedSongEntity;
import com.mymusic.backend.model.PlaylistEntity;
import com.mymusic.backend.model.PlaylistTrackEntity;
import com.mymusic.backend.repository.LikedSongRepository;
import com.mymusic.backend.repository.PlaylistRepository;
import com.mymusic.backend.repository.PlaylistTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final LikedSongRepository likedSongRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;

    @Autowired
    public LibraryService(LikedSongRepository likedSongRepository,
                          PlaylistRepository playlistRepository,
                          PlaylistTrackRepository playlistTrackRepository) {
        this.likedSongRepository = likedSongRepository;
        this.playlistRepository = playlistRepository;
        this.playlistTrackRepository = playlistTrackRepository;
    }

    public List<LikedSongEntity> getLikedSongs(String userId) {
        return likedSongRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public LikedSongEntity toggleLikedSong(LikedSongEntity song) {
        Optional<LikedSongEntity> existing = likedSongRepository.findByUserIdAndTrackId(song.getUserId(), song.getTrackId());
        if (existing.isPresent()) {
            likedSongRepository.delete(existing.get());
            return null; // Removed
        } else {
            return likedSongRepository.save(song); // Added
        }
    }

    @Transactional
    public void deleteLikedSong(String userId, String trackId) {
        likedSongRepository.deleteByUserIdAndTrackId(userId, trackId);
    }

    public List<PlaylistEntity> getPlaylists(String userId) {
        return playlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public PlaylistEntity createPlaylist(PlaylistEntity playlist) {
        return playlistRepository.save(playlist);
    }

    @Transactional
    public void deletePlaylist(String playlistId, String userId) {
        playlistTrackRepository.deleteByPlaylistId(playlistId);
        playlistRepository.deleteByIdAndUserId(playlistId, userId);
    }

    public List<PlaylistTrackEntity> getPlaylistTracks(String playlistId) {
        return playlistTrackRepository.findByPlaylistIdOrderByCreatedAtDesc(playlistId);
    }

    public PlaylistTrackEntity addTrackToPlaylist(PlaylistTrackEntity track) {
        return playlistTrackRepository.save(track);
    }

    @Transactional
    public void removeTrackFromPlaylist(String playlistId, String trackId) {
        playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId);
    }
}

