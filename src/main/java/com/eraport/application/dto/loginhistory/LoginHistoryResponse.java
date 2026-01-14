package com.eraport.application.dto.loginhistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginHistoryResponse {
    private UUID id;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
    private String userAgent;
    private Boolean isSuccess;
    private Boolean isLoggedOut;
    private LocalDateTime tokenExpired;
}
