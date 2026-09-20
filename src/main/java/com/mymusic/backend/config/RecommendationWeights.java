package com.mymusic.backend.config;

/**
 * Configurable scoring and interaction weight constants for the hybrid recommendation engine.
 */
public class RecommendationWeights {

    // Algorithm scoring weights (Sum = 1.0)
    public static final double WEIGHT_LANGUAGE = 0.25;
    public static final double WEIGHT_ARTIST = 0.25;
    public static final double WEIGHT_GENRE = 0.20;
    public static final double WEIGHT_LIKE_SIMILARITY = 0.15;
    public static final double WEIGHT_HISTORY = 0.10;
    public static final double WEIGHT_POPULARITY = 0.05;

    // Behavioral event score additions/subtractions
    public static final double SCORE_PLAY_30S = 1.0;
    public static final double SCORE_COMPLETE = 3.0;
    public static final double SCORE_LIKE = 5.0;
    public static final double SCORE_REPLAY = 4.0;
    public static final double SCORE_ADD_TO_PLAYLIST = 5.0;
    public static final double SCORE_SKIP = -3.0;
    public static final double SCORE_EARLY_SKIP = -1.0;
}
