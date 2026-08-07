package com.mymusic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mymusic.backend.model.SongEntity;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, String> {
}
