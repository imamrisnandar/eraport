package com.eraport.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private String type = "Bearer";

    private UUID userId;

    private String username;

    private String email;

    private String fullName;

    private LocalDateTime expiresAt;
}
