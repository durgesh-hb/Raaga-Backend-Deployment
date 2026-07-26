package com.mymusic.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mymusic.backend.dto.SongDTO;
import com.mymusic.backend.service.MusicService;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/test")
    public String test() {
        return "MyMusic Backend is working!";
    }

    @GetMapping("/search")
    public List<SongDTO> searchSongs(@RequestParam String query) {
        return musicService.searchSongs(query);
    }

    @GetMapping("/song/{id}")
    public SongDTO getSongById(@PathVariable String id) {
        return musicService.getSongById(id);
    }
}