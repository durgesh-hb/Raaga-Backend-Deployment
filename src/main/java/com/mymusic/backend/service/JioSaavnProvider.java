package com.mymusic.backend.service;

import com.mymusic.backend.dto.SongDTO;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JioSaavnProvider implements MusicProviderService {

    private final MusicService musicService;

    public JioSaavnProvider(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public List<SongDTO> search(String query) {
        return musicService.searchSongs(query);
    }

    @Override
    public SongDTO getTrack(String id) {
        return musicService.getSongById(id);
    }
}
