package com.eraport.infrastructure.controllers.loginhistory;

import com.eraport.application.dto.shared.ApiResponse;
import com.eraport.application.dto.loginhistory.LoginHistoryResponse;
import com.eraport.domain.entities.LoginHistory;
import com.eraport.domain.services.loginhistory.LoginHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login-history")
@RequiredArgsConstructor
@Tag(name = "Login History", description = "Login history APIs")
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user login history")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getUserLoginHistory(@PathVariable UUID userId) {
        List<LoginHistory> histories = loginHistoryService.getUserLoginHistory(userId);

        List<LoginHistoryResponse> responses = histories.stream()
                .map(this::mapToLoginHistoryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}/active-sessions")
    @Operation(summary = "Get user active sessions")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getActiveSessions(@PathVariable UUID userId) {
        List<LoginHistory> activeSessions = loginHistoryService.getActiveSessions(userId);

        List<LoginHistoryResponse> responses = activeSessions.stream()
                .map(this::mapToLoginHistoryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    private LoginHistoryResponse mapToLoginHistoryResponse(LoginHistory history) {
        return LoginHistoryResponse.builder()
                .id(history.getId())
                .loginTime(history.getLoginTime())
                .logoutTime(history.getLogoutTime())
                .ipAddress(history.getIpAddress())
                .userAgent(history.getUserAgent())
                .isSuccess(history.getIsSuccess())
                .isLoggedOut(history.getIsLoggedOut())
                .tokenExpired(history.getTokenExpired())
                .build();
    }
}
