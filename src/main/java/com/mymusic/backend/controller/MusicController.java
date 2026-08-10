package com.mymusic.backend.controller;

import com.mymusic.backend.dto.SongDTO;
import com.mymusic.backend.service.MusicProviderService;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping
public class MusicController {

    private final MusicProviderService musicProviderService;
    private final DataSource dataSource;

    @Autowired
    public MusicController(MusicProviderService musicProviderService, DataSource dataSource) {
        this.musicProviderService = musicProviderService;
        this.dataSource = dataSource;
    }

    @GetMapping("/api/music/test")
    public String test() {
        return "MyMusic Backend is working!";
    }

    @GetMapping("/api/music/db-check")
    public String dbCheck() {
        try (Connection conn = dataSource.getConnection()) {
            return "✅ Supabase PostgreSQL Connected Successfully! Database: " + conn.getCatalog() + " | Product: " + conn.getMetaData().getDatabaseProductName();
        } catch (Exception e) {
            return "❌ Supabase PostgreSQL Connection Failed: " + e.getMessage();
        }
    }

    @GetMapping({"/api/v1/music/search", "/api/music/search"})
    public List<SongDTO> searchSongs(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "query", required = false) String query) {
        String searchQuery = (q != null && !q.trim().isEmpty()) ? q : query;
        if (searchQuery == null) {
            searchQuery = "";
        }
        return musicProviderService.search(searchQuery);
    }

    @GetMapping({"/api/v1/music/track/{id}", "/api/music/song/{id}"})
    public SongDTO getTrackById(@PathVariable("id") String id) {
        return musicProviderService.getTrack(id);
    }
}