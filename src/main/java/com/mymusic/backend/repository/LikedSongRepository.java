package com.mymusic.backend.repository;

import com.mymusic.backend.model.LikedSongEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSongEntity, String> {
    List<LikedSongEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<LikedSongEntity> findByUserIdAndTrackId(String userId, String trackId);
    void deleteByUserIdAndTrackId(String userId, String trackId);
}
