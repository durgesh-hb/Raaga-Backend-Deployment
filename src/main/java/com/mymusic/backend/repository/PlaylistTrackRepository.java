package com.mymusic.backend.repository;

import com.mymusic.backend.model.PlaylistTrackEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrackEntity, String> {
    List<PlaylistTrackEntity> findByPlaylistIdOrderByCreatedAtAsc(String playlistId);
    List<PlaylistTrackEntity> findByPlaylistIdOrderByCreatedAtDesc(String playlistId);
    long countByPlaylistId(String playlistId);
    void deleteByPlaylistIdAndTrackId(String playlistId, String trackId);
    void deleteByPlaylistId(String playlistId);
}

