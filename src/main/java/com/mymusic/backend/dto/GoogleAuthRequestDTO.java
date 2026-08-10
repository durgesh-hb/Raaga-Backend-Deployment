package com.mymusic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequestDTO {
    private String idToken;
    private String authCode;
    private String redirectUri;
    private String email;
    private String name;
}
