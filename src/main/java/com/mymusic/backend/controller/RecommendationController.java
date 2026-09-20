package com.mymusic.backend.controller;

import com.mymusic.backend.dto.RecommendationDTOs.*;
import com.mymusic.backend.model.UserPreferenceEntity;
import com.mymusic.backend.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * POST /api/v1/recommendations/preferences
     * Save or update onboarding user preferences (languages, artists, genres, songs)
     */
    @PostMapping("/preferences")
    public ResponseEntity<Void> savePreferences(@RequestBody OnboardingPreferencesRequest request) {
        recommendationService.saveUserPreferences(request);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/recommendations/preferences
     * Retrieve stored preferences for a user
     */
    @GetMapping("/preferences")
    public ResponseEntity<List<UserPreferenceEntity>> getPreferences(@RequestParam(defaultValue = "user_default") String userId) {
        return ResponseEntity.ok(recommendationService.getUserPreferences(userId));
    }

    /**
     * POST /api/v1/recommendations/event
     * Record real-time user playback action (PLAY, COMPLETE, REPLAY, LIKE, SKIP, EARLY_SKIP)
     */
    @PostMapping("/event")
    public ResponseEntity<Void> recordListeningEvent(@RequestBody ListeningEventRequest request) {
        recommendationService.recordListeningEvent(request);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/recommendations/home
     * Retrieve dynamic personalized recommendation sections for the home screen
     */
    @GetMapping("/home")
    public ResponseEntity<HomeRecommendationResponseDTO> getHomeRecommendations(@RequestParam(defaultValue = "user_default") String userId) {
        return ResponseEntity.ok(recommendationService.getHomeRecommendations(userId));
    }
}
