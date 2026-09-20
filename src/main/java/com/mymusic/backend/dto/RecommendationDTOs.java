package com.mymusic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecommendationDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnboardingPreferencesRequest {
        private String userId;
        private List<String> languages;
        private List<String> artists;
        private List<String> genres;
        private List<String> songs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListeningEventRequest {
        private String userId;
        private String trackId;
        private String title;
        private String artist;
        private String language;
        private String genre;
        private Integer playedSeconds;
        private Integer duration;
        private Boolean completed;
        private String action; // PLAY, PAUSE, SKIP, EARLY_SKIP, COMPLETE, LIKE, REPLAY, ADD_TO_PLAYLIST
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationSectionDTO {
        private String id;
        private String title;
        private String description;
        private String sectionType; // RECOMMENDED_FOR_YOU, BECAUSE_YOU_LIKE_ARTIST, POPULAR_IN_LANGUAGE, RECENTLY_PLAYED, DISCOVER
        private List<SongDTO> songs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeRecommendationResponseDTO {
        private String userId;
        private Boolean hasPreferences;
        private List<RecommendationSectionDTO> sections;
    }
}
