package com.mymusic.backend.service;

import com.mymusic.backend.config.RecommendationWeights;
import com.mymusic.backend.dto.RecommendationDTOs.*;
import com.mymusic.backend.dto.SongDTO;
import com.mymusic.backend.model.LikedSongEntity;
import com.mymusic.backend.model.ListeningHistoryEntity;
import com.mymusic.backend.model.UserPreferenceEntity;
import com.mymusic.backend.repository.LikedSongRepository;
import com.mymusic.backend.repository.ListeningHistoryRepository;
import com.mymusic.backend.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final ListeningHistoryRepository listeningHistoryRepository;
    private final LikedSongRepository likedSongRepository;
    private final MusicService musicService;

    public RecommendationService(UserPreferenceRepository userPreferenceRepository,
                                 ListeningHistoryRepository listeningHistoryRepository,
                                 LikedSongRepository likedSongRepository,
                                 MusicService musicService) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.likedSongRepository = likedSongRepository;
        this.musicService = musicService;
    }

    // =====================================================
    // 1. SAVE ONBOARDING PREFERENCES (COLD START)
    // =====================================================

    @Transactional
    public void saveUserPreferences(OnboardingPreferencesRequest request) {
        String userId = (request.getUserId() != null && !request.getUserId().isBlank())
                ? request.getUserId()
                : "user_default";

        // Remove existing explicit preferences to allow updating
        userPreferenceRepository.deleteByUserId(userId);

        List<UserPreferenceEntity> entities = new ArrayList<>();

        if (request.getLanguages() != null) {
            for (String lang : request.getLanguages()) {
                if (lang != null && !lang.isBlank()) {
                    entities.add(UserPreferenceEntity.builder()
                            .userId(userId)
                            .preferenceType("LANGUAGE")
                            .preferenceValue(lang.trim())
                            .score(1.0)
                            .build());
                }
            }
        }

        if (request.getArtists() != null) {
            for (String artist : request.getArtists()) {
                if (artist != null && !artist.isBlank()) {
                    entities.add(UserPreferenceEntity.builder()
                            .userId(userId)
                            .preferenceType("ARTIST")
                            .preferenceValue(artist.trim())
                            .score(1.0)
                            .build());
                }
            }
        }

        if (request.getGenres() != null) {
            for (String genre : request.getGenres()) {
                if (genre != null && !genre.isBlank()) {
                    entities.add(UserPreferenceEntity.builder()
                            .userId(userId)
                            .preferenceType("GENRE")
                            .preferenceValue(genre.trim())
                            .score(1.0)
                            .build());
                }
            }
        }

        if (request.getSongs() != null) {
            for (String song : request.getSongs()) {
                if (song != null && !song.isBlank()) {
                    entities.add(UserPreferenceEntity.builder()
                            .userId(userId)
                            .preferenceType("SONG")
                            .preferenceValue(song.trim())
                            .score(1.0)
                            .build());
                }
            }
        }

        if (!entities.isEmpty()) {
            userPreferenceRepository.saveAll(entities);
        }
    }

    public List<UserPreferenceEntity> getUserPreferences(String userId) {
        String cleanUserId = (userId != null && !userId.isBlank()) ? userId : "user_default";
        return userPreferenceRepository.findByUserId(cleanUserId);
    }

    // =====================================================
    // 2. RECORD LISTENING EVENT (BEHAVIORAL SIGNALS)
    // =====================================================

    @Transactional
    public void recordListeningEvent(ListeningEventRequest request) {
        String userId = (request.getUserId() != null && !request.getUserId().isBlank())
                ? request.getUserId()
                : "user_default";

        ListeningHistoryEntity history = ListeningHistoryEntity.builder()
                .userId(userId)
                .trackId(request.getTrackId() != null ? request.getTrackId() : UUID.randomUUID().toString())
                .title(request.getTitle())
                .artist(request.getArtist())
                .language(request.getLanguage())
                .genre(request.getGenre())
                .playedSeconds(request.getPlayedSeconds())
                .duration(request.getDuration())
                .completed(request.getCompleted())
                .action(request.getAction())
                .build();

        listeningHistoryRepository.save(history);

        // Update behavioral preference score for artist & language if available
        double scoreDelta = calculateEventScore(request.getAction());
        if (scoreDelta != 0.0) {
            if (request.getArtist() != null && !request.getArtist().isBlank()) {
                updatePreferenceScore(userId, "ARTIST", request.getArtist(), scoreDelta);
            }
            if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
                updatePreferenceScore(userId, "LANGUAGE", request.getLanguage(), scoreDelta);
            }
        }
    }

    private double calculateEventScore(String action) {
        if (action == null) return 0.0;
        switch (action.toUpperCase()) {
            case "PLAY_30S":
            case "PLAY":
                return RecommendationWeights.SCORE_PLAY_30S;
            case "COMPLETE":
                return RecommendationWeights.SCORE_COMPLETE;
            case "LIKE":
                return RecommendationWeights.SCORE_LIKE;
            case "REPLAY":
                return RecommendationWeights.SCORE_REPLAY;
            case "ADD_TO_PLAYLIST":
                return RecommendationWeights.SCORE_ADD_TO_PLAYLIST;
            case "SKIP":
                return RecommendationWeights.SCORE_SKIP;
            case "EARLY_SKIP":
                return RecommendationWeights.SCORE_EARLY_SKIP;
            default:
                return 0.0;
        }
    }

    private void updatePreferenceScore(String userId, String type, String value, double delta) {
        List<UserPreferenceEntity> list = userPreferenceRepository.findByUserIdAndPreferenceType(userId, type);
        Optional<UserPreferenceEntity> match = list.stream()
                .filter(p -> p.getPreferenceValue().equalsIgnoreCase(value))
        .findFirst();

        if (match.isPresent()) {
            UserPreferenceEntity pref = match.get();
            pref.setScore(Math.max(0.1, pref.getScore() + delta));
            userPreferenceRepository.save(pref);
        } else {
            userPreferenceRepository.save(UserPreferenceEntity.builder()
                    .userId(userId)
                    .preferenceType(type)
                    .preferenceValue(value)
                    .score(Math.max(0.1, 1.0 + delta))
                    .build());
        }
    }

    // =====================================================
    // 3. GENERATE HOME RECOMMENDATIONS (HYBRID ALGORITHM)
    // =====================================================

    public HomeRecommendationResponseDTO getHomeRecommendations(String userId) {
        String cleanUserId = (userId != null && !userId.isBlank()) ? userId : "user_default";

        List<UserPreferenceEntity> prefs = userPreferenceRepository.findByUserId(cleanUserId);
        List<LikedSongEntity> likedSongs = likedSongRepository.findByUserIdOrderByCreatedAtDesc(cleanUserId);
        List<ListeningHistoryEntity> recentHistory = listeningHistoryRepository.findTop50ByUserIdOrderByPlayedAtDesc(cleanUserId);

        boolean hasPreferences = !prefs.isEmpty() || !likedSongs.isEmpty() || !recentHistory.isEmpty();

        // Extract preferred languages and artists
        Set<String> preferredLanguages = new LinkedHashSet<>();
        Set<String> preferredArtists = new LinkedHashSet<>();
        Set<String> preferredGenres = new LinkedHashSet<>();

        for (UserPreferenceEntity pref : prefs) {
            if ("LANGUAGE".equalsIgnoreCase(pref.getPreferenceType())) {
                preferredLanguages.add(pref.getPreferenceValue());
            } else if ("ARTIST".equalsIgnoreCase(pref.getPreferenceType())) {
                preferredArtists.add(pref.getPreferenceValue());
            } else if ("GENRE".equalsIgnoreCase(pref.getPreferenceType())) {
                preferredGenres.add(pref.getPreferenceValue());
            }
        }

        // Add artists & languages from liked songs
        for (LikedSongEntity song : likedSongs) {
            if (song.getArtist() != null && !song.getArtist().isBlank()) {
                preferredArtists.add(song.getArtist().split(",")[0].trim());
            }
        }

        // Add top listened artists
        Map<String, Long> historyArtistCounts = recentHistory.stream()
                .filter(h -> h.getArtist() != null)
                .collect(Collectors.groupingBy(ListeningHistoryEntity::getArtist, Collectors.counting()));
        historyArtistCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> preferredArtists.add(e.getKey().split(",")[0].trim()));

        // Default fallbacks if user is brand new without preferences
        if (preferredLanguages.isEmpty()) {
            preferredLanguages.add("Kannada");
            preferredLanguages.add("Hindi");
        }
        if (preferredArtists.isEmpty()) {
            preferredArtists.add("Arijit Singh");
            preferredArtists.add("Anirudh Ravichander");
            preferredArtists.add("Puneeth Rajkumar");
        }

        List<RecommendationSectionDTO> sections = new ArrayList<>();
        Set<String> seenTrackIds = new HashSet<>();

        // Section 1: Recommended For You (Scored hybrid pool)
        List<SongDTO> candidatePool = fetchCandidateSongs(preferredLanguages, preferredArtists);
        List<SongDTO> scoredForYou = scoreAndFilterCandidates(candidatePool, preferredLanguages, preferredArtists, preferredGenres, likedSongs, recentHistory, seenTrackIds, 10);

        if (!scoredForYou.isEmpty()) {
            sections.add(RecommendationSectionDTO.builder()
                    .id("sec_recommended")
                    .title("Recommended for You")
                    .description("Personalized mix based on your preferences & listening style")
                    .sectionType("RECOMMENDED_FOR_YOU")
                    .songs(scoredForYou)
                    .build());
        }

        // Section 2: Because You Like [Artist]
        String topArtist = preferredArtists.iterator().next();
        List<SongDTO> artistSongs = musicService.searchSongs(topArtist);
        List<SongDTO> filteredArtistSongs = filterAndLimitSongs(artistSongs, seenTrackIds, 8);
        if (!filteredArtistSongs.isEmpty()) {
            sections.add(RecommendationSectionDTO.builder()
                    .id("sec_artist_" + topArtist.replaceAll("\\s+", "_").toLowerCase())
                    .title("Because You Like " + topArtist)
                    .description("Top hits & feature tracks by " + topArtist)
                    .sectionType("BECAUSE_YOU_LIKE_ARTIST")
                    .songs(filteredArtistSongs)
                    .build());
        }

        // Section 3: Popular in [Language]
        String topLang = preferredLanguages.iterator().next();
        List<SongDTO> langSongs = musicService.searchSongs("Top " + topLang + " Songs");
        List<SongDTO> filteredLangSongs = filterAndLimitSongs(langSongs, seenTrackIds, 8);
        if (!filteredLangSongs.isEmpty()) {
            sections.add(RecommendationSectionDTO.builder()
                    .id("sec_lang_" + topLang.toLowerCase())
                    .title("Popular in " + topLang)
                    .description("Trending chart toppers in " + topLang)
                    .sectionType("POPULAR_IN_LANGUAGE")
                    .songs(filteredLangSongs)
                    .build());
        }

        // Section 4: Recently Played (from Listening History)
        if (!recentHistory.isEmpty()) {
            List<SongDTO> recentSongs = new ArrayList<>();
            Set<String> recentIds = new HashSet<>();
            for (ListeningHistoryEntity h : recentHistory) {
                if (recentIds.add(h.getTrackId())) {
                    recentSongs.add(new SongDTO(
                            h.getTrackId(),
                            h.getTitle() != null ? h.getTitle() : "Track",
                            h.getArtist() != null ? h.getArtist() : "Artist",
                            "Single",
                            "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&auto=format&fit=crop&q=80",
                            "",
                            h.getDuration() != null ? h.getDuration() : 180,
                            h.getLanguage() != null ? h.getLanguage() : topLang
                    ));
                }
                if (recentSongs.size() >= 8) break;
            }
            if (!recentSongs.isEmpty()) {
                sections.add(RecommendationSectionDTO.builder()
                        .id("sec_recently_played")
                        .title("Recently Played")
                        .description("Pick up right where you left off")
                        .sectionType("RECENTLY_PLAYED")
                        .songs(recentSongs)
                        .build());
            }
        }

        // Section 5: Discover Something New
        List<SongDTO> discoverPool = musicService.searchSongs("Indie Hits");
        List<SongDTO> filteredDiscover = filterAndLimitSongs(discoverPool, seenTrackIds, 8);
        if (filteredDiscover.isEmpty()) {
            filteredDiscover = filterAndLimitSongs(musicService.searchSongs("Latest Releases"), seenTrackIds, 8);
        }
        if (!filteredDiscover.isEmpty()) {
            sections.add(RecommendationSectionDTO.builder()
                    .id("sec_discover")
                    .title("Discover Something New")
                    .description("Fresh tracks and hidden gems curated for exploration")
                    .sectionType("DISCOVER")
                    .songs(filteredDiscover)
                    .build());
        }

        return HomeRecommendationResponseDTO.builder()
                .userId(cleanUserId)
                .hasPreferences(hasPreferences)
                .sections(sections)
                .build();
    }

    // =====================================================
    // HELPER METHODS: CANDIDATE FETCHING & HYBRID SCORING
    // =====================================================

    private List<SongDTO> fetchCandidateSongs(Set<String> languages, Set<String> artists) {
        List<SongDTO> candidates = new ArrayList<>();

        for (String artist : artists) {
            candidates.addAll(musicService.searchSongs(artist));
        }
        for (String lang : languages) {
            candidates.addAll(musicService.searchSongs("Top " + lang + " Songs"));
        }

        return candidates;
    }

    private List<SongDTO> scoreAndFilterCandidates(List<SongDTO> candidates,
                                                   Set<String> languages,
                                                   Set<String> artists,
                                                   Set<String> genres,
                                                   List<LikedSongEntity> likedSongs,
                                                   List<ListeningHistoryEntity> history,
                                                   Set<String> seenTrackIds,
                                                   int limit) {
        Set<String> likedTrackIds = likedSongs.stream().map(LikedSongEntity::getTrackId).collect(Collectors.toSet());
        Set<String> recentTrackIds = history.stream().map(ListeningHistoryEntity::getTrackId).collect(Collectors.toSet());

        Map<SongDTO, Double> scoredMap = new LinkedHashMap<>();
        Map<String, Integer> artistCountMap = new HashMap<>();

        for (SongDTO song : candidates) {
            if (song == null || song.getId() == null) continue;

            // Compute hybrid recommendation score formula
            double langScore = matchLanguage(song.getLanguage(), languages) ? 1.0 : 0.0;
            double artistScore = matchArtist(song.getArtist(), artists) ? 1.0 : 0.0;
            double genreScore = matchGenre(song.getTitle(), genres) ? 1.0 : 0.0;
            double likeScore = likedTrackIds.contains(song.getId()) ? 1.0 : 0.0;
            double historyScore = recentTrackIds.contains(song.getId()) ? 0.5 : 0.0;
            double popularityScore = 0.8; // Baseline popularity score

            double totalScore = (langScore * RecommendationWeights.WEIGHT_LANGUAGE)
                    + (artistScore * RecommendationWeights.WEIGHT_ARTIST)
                    + (genreScore * RecommendationWeights.WEIGHT_GENRE)
                    + (likeScore * RecommendationWeights.WEIGHT_LIKE_SIMILARITY)
                    + (historyScore * RecommendationWeights.WEIGHT_HISTORY)
                    + (popularityScore * RecommendationWeights.WEIGHT_POPULARITY);

            scoredMap.put(song, totalScore);
        }

        // Sort songs by score descending
        List<Map.Entry<SongDTO, Double>> sortedEntries = new ArrayList<>(scoredMap.entrySet());
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<SongDTO> result = new ArrayList<>();
        for (Map.Entry<SongDTO, Double> entry : sortedEntries) {
            SongDTO song = entry.getKey();
            if (seenTrackIds.contains(song.getId())) continue;

            // Cap max 3 tracks per artist in Recommended for You to maintain diversity
            String primaryArtist = song.getArtist() != null ? song.getArtist().split(",")[0].trim() : "Unknown";
            int currentCount = artistCountMap.getOrDefault(primaryArtist, 0);
            if (currentCount >= 3) continue;

            artistCountMap.put(primaryArtist, currentCount + 1);
            seenTrackIds.add(song.getId());
            result.add(song);

            if (result.size() >= limit) break;
        }

        return result;
    }

    private List<SongDTO> filterAndLimitSongs(List<SongDTO> songs, Set<String> seenTrackIds, int limit) {
        List<SongDTO> filtered = new ArrayList<>();
        if (songs == null) return filtered;

        for (SongDTO song : songs) {
            if (song == null || song.getId() == null) continue;
            if (!seenTrackIds.contains(song.getId())) {
                seenTrackIds.add(song.getId());
                filtered.add(song);
                if (filtered.size() >= limit) break;
            }
        }

        return filtered;
    }

    private boolean matchLanguage(String songLang, Set<String> preferredLangs) {
        if (songLang == null || preferredLangs == null) return false;
        for (String lang : preferredLangs) {
            if (songLang.equalsIgnoreCase(lang)) return true;
        }
        return false;
    }

    private boolean matchArtist(String songArtist, Set<String> preferredArtists) {
        if (songArtist == null || preferredArtists == null) return false;
        for (String artist : preferredArtists) {
            if (songArtist.toLowerCase().contains(artist.toLowerCase())) return true;
        }
        return false;
    }

    private boolean matchGenre(String title, Set<String> preferredGenres) {
        if (title == null || preferredGenres == null) return false;
        for (String g : preferredGenres) {
            if (title.toLowerCase().contains(g.toLowerCase())) return true;
        }
        return false;
    }
}
