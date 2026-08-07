package com.mymusic.backend.controller;

import com.mymusic.backend.dto.AuthResponseDTO;
import com.mymusic.backend.dto.GoogleAuthRequestDTO;
import com.mymusic.backend.service.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GoogleAuthService googleAuthService;

    public AuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    /**
     * Endpoint returning Google OAuth redirect URL configuration
     */
    @GetMapping("/google/url")
    public ResponseEntity<Map<String, String>> getGoogleAuthUrl(@RequestParam(required = false) String redirectUri) {
        String authUrl = googleAuthService.getGoogleOAuthConsentUrl(redirectUri);
        Map<String, String> response = new HashMap<>();
        response.put("url", authUrl);
        response.put("provider", "google");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to verify Google ID token / Auth Code and complete OAuth login flow
     */
    @PostMapping("/google/verify")
    public ResponseEntity<AuthResponseDTO> verifyGoogleToken(@RequestBody GoogleAuthRequestDTO request) {
        AuthResponseDTO response = googleAuthService.verifyAndAuthenticateGoogleUser(request);
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint to receive OAuth authorization code callback
     */
    @PostMapping("/google/callback")
    public ResponseEntity<AuthResponseDTO> handleGoogleCallback(@RequestBody GoogleAuthRequestDTO request) {
        AuthResponseDTO response = googleAuthService.verifyAndAuthenticateGoogleUser(request);
        return ResponseEntity.ok(response);
    }
}
