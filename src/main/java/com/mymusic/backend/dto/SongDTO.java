package com.mymusic.backend.dto;

public class SongDTO {

    private String id;
    private String name;
    private String artist;
    private String imageUrl;
    private String audioUrl;
    private int duration;
    private String language;

    public SongDTO(String id, String name, String artist,
                   String imageUrl, String audioUrl,
                   int duration, String language) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.duration = duration;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public int getDuration() {
        return duration;
    }

    public String getLanguage() {
        return language;
    }
}