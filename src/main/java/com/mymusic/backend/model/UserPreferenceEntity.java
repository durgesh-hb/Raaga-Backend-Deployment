package com.mymusic.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "preference_type", "preference_value"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "preference_type", nullable = false)
    private String preferenceType; // LANGUAGE, ARTIST, GENRE, SONG

    @Column(name = "preference_value", nullable = false)
    private String preferenceValue;

    @Column(name = "score")
    @Builder.Default
    private Double score = 1.0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.score == null) {
            this.score = 1.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
