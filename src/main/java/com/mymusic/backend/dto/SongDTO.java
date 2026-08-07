package com.mymusic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {

    private String id;
    private String title;
    private String artist;
    private String album;
    private String artworkUrl;
    private String streamUrl;
    private int duration;
    private String language;

    // Custom constructor matching { id, name, artist, imageUrl, audioUrl, duration, language }
    public SongDTO(String id, String name, String artist, String imageUrl, String audioUrl, int duration, String language) {
        this.id = id;
        this.title = name;
        this.artist = artist;
        this.album = "Single";
        this.artworkUrl = imageUrl;
        this.streamUrl = audioUrl;
        this.duration = duration;
        this.language = language;
    }

    // Custom constructor matching { id, title, artist, album, artworkUrl, streamUrl, duration }
    public SongDTO(String id, String title, String artist, String album, String artworkUrl, String streamUrl, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album != null ? album : "Single";
        this.artworkUrl = artworkUrl;
        this.streamUrl = streamUrl;
        this.duration = duration;
        this.language = "Hindi";
    }

    // Legacy getter aliases for compatibility
    public String getName() {
        return title;
    }

    public String getImageUrl() {
        return artworkUrl;
    }

    public String getAudioUrl() {
        return streamUrl;
    }
}