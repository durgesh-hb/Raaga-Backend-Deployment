package com.mymusic.backend.repository;

import com.mymusic.backend.model.PlaylistEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, String> {
    List<PlaylistEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}
