package com.mymusic.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongEntity {

    @Id
    private String id;
    private String name;
    private String artist;
    private String imageUrl;
    private String audioUrl;
    private int duration;
    private String language;
}
