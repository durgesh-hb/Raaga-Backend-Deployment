package com.mymusic.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private boolean authenticated;
    private String token;
    private String userId;
    private String email;
    private String name;
    private String picture;
    private String provider;
    private String message;
}
