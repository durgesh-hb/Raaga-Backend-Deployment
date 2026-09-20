package com.mymusic.backend.repository;

import com.mymusic.backend.model.UserPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreferenceEntity, String> {

    List<UserPreferenceEntity> findByUserId(String userId);

    List<UserPreferenceEntity> findByUserIdAndPreferenceType(String userId, String preferenceType);

    void deleteByUserId(String userId);

    void deleteByUserIdAndPreferenceType(String userId, String preferenceType);
}
