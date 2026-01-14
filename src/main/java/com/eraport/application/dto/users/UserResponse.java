package com.eraport.application.dto.users;

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
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String fullName;

    private Boolean isActive;

    private Boolean isRevoked;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
