package com.mymusic.backend.service;

import com.mymusic.backend.dto.SongDTO;
import java.util.List;

public interface MusicProviderService {
    List<SongDTO> search(String query);
    SongDTO getTrack(String id);
}
