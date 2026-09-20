package com.mymusic.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "listening_history", indexes = {
    @Index(name = "idx_lh_user_id", columnList = "user_id"),
    @Index(name = "idx_lh_played_at", columnList = "played_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListeningHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "track_id", nullable = false)
    private String trackId;

    @Column(name = "title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "language")
    private String language;

    @Column(name = "genre")
    private String genre;

    @Column(name = "played_seconds")
    private Integer playedSeconds;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "action")
    private String action; // PLAY, PAUSE, SKIP, EARLY_SKIP, COMPLETE, LIKE, REPLAY, ADD_TO_PLAYLIST

    @Column(name = "played_at")
    private LocalDateTime playedAt;

    @PrePersist
    protected void onCreate() {
        if (this.playedAt == null) {
            this.playedAt = LocalDateTime.now();
        }
    }
}
