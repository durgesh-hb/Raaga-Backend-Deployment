package com.mymusic.backend.service;

import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mymusic.backend.dto.SongDTO;

@Service
public class MusicService {

    private final RestClient restClient;

    // Search results cache
    private final Map<String, CacheEntry> cache = new HashMap<>();

    // 10 minutes in milliseconds
    private static final long CACHE_DURATION = 10 * 60 * 1000;

    public MusicService(
            @Value("${music.api.base-url}") String musicApiBaseUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(musicApiBaseUrl)
                .build();
    }

    // =====================================================
    // SEARCH SONGS
    // =====================================================

    public List<SongDTO> searchSongs(String query) {

        String cacheKey = query.trim().toLowerCase();

        // Check cache
        CacheEntry cachedEntry = cache.get(cacheKey);

        if (cachedEntry != null) {

            long currentTime = System.currentTimeMillis();

            // Check if cache is still valid
            if (currentTime - cachedEntry.createdAt < CACHE_DURATION) {

                System.out.println("CACHE HIT: " + cacheKey);

                return cachedEntry.songs;
            }

            // Cache expired
            System.out.println("CACHE EXPIRED: " + cacheKey);

            cache.remove(cacheKey);
        }

        System.out.println("API CALL: " + cacheKey);

        // Call local JioSaavn API
        Map response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/search/songs")
                        .queryParam("query", query)
                        .queryParam("limit", 20)
                        .build())
                .retrieve()
                .body(Map.class);

        List<SongDTO> songs = new ArrayList<>();

        if (response == null) {
            return songs;
        }

        Map data = (Map) response.get("data");

        if (data == null) {
            return songs;
        }

        List<Map> results = (List<Map>) data.get("results");

        if (results == null) {
            return songs;
        }

        for (Map song : results) {
            songs.add(convertToSongDTO(song));
        }

        // Save results with current timestamp
        cache.put(
                cacheKey,
                new CacheEntry(songs, System.currentTimeMillis())
        );

        System.out.println("CACHE SAVED: " + cacheKey);

        return songs;
    }

    // =====================================================
    // GET SONG BY ID
    // =====================================================

    public SongDTO getSongById(String id) {

        Map response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/songs")
                        .queryParam("ids", id)
                        .build())
                .retrieve()
                .body(Map.class);

        if (response == null) {
            return null;
        }

        Object dataObject = response.get("data");

        if (!(dataObject instanceof List)) {
            return null;
        }

        List<Map> songs = (List<Map>) dataObject;

        if (songs.isEmpty()) {
            return null;
        }

        return convertToSongDTO(songs.get(0));
    }

    // =====================================================
    // CONVERT JIOSAAVN SONG -> SONG DTO
    // =====================================================

    private SongDTO convertToSongDTO(Map song) {

        String id = (String) song.get("id");
        String name = (String) song.get("name");
        String language = (String) song.get("language");

        Number durationValue = (Number) song.get("duration");

        int duration = durationValue != null
                ? durationValue.intValue()
                : 0;

        // -------------------------
        // Artists
        // -------------------------

        String artist = "Unknown Artist";

        Map artists = (Map) song.get("artists");

        if (artists != null) {

            List<Map> primaryArtists =
                    (List<Map>) artists.get("primary");

            if (primaryArtists != null && !primaryArtists.isEmpty()) {

                List<String> artistNames = new ArrayList<>();

                for (Map artistData : primaryArtists) {

                    String artistName =
                            (String) artistData.get("name");

                    if (artistName != null) {
                        artistNames.add(artistName);
                    }
                }

                artist = String.join(", ", artistNames);
            }
        }

        // -------------------------
        // Highest quality image
        // -------------------------

        String imageUrl = null;

        List<Map> images = (List<Map>) song.get("image");

        if (images != null && !images.isEmpty()) {

            imageUrl = (String) images
                    .get(images.size() - 1)
                    .get("url");
        }

        // -------------------------
        // Highest quality audio
        // -------------------------

        String audioUrl = null;

        List<Map> downloadUrls =
                (List<Map>) song.get("downloadUrl");

        if (downloadUrls != null && !downloadUrls.isEmpty()) {

            audioUrl = (String) downloadUrls
                    .get(downloadUrls.size() - 1)
                    .get("url");
        }

        return new SongDTO(
                id,
                name,
                artist,
                imageUrl,
                audioUrl,
                duration,
                language
        );
    }

    // =====================================================
    // CACHE ENTRY
    // =====================================================

    private static class CacheEntry {

        private final List<SongDTO> songs;
        private final long createdAt;

        public CacheEntry(List<SongDTO> songs, long createdAt) {
            this.songs = songs;
            this.createdAt = createdAt;
        }
    }
}