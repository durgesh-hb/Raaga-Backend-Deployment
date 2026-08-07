package com.mymusic.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "liked_songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikedSongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "track_id", nullable = false)
    private String trackId;

    private String title;

    private String artist;

    @Column(name = "artwork_url")
    private String artworkUrl;

    @Column(name = "stream_url")
    private String streamUrl;

    private Integer duration;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
