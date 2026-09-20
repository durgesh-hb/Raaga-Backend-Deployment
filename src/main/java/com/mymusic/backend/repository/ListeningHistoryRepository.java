package com.mymusic.backend.repository;

import com.mymusic.backend.model.ListeningHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListeningHistoryEntity, String> {

    List<ListeningHistoryEntity> findByUserIdOrderByPlayedAtDesc(String userId);

    List<ListeningHistoryEntity> findTop50ByUserIdOrderByPlayedAtDesc(String userId);

    List<ListeningHistoryEntity> findTop20ByUserIdAndActionOrderByPlayedAtDesc(String userId, String action);
}
