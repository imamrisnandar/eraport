package com.eraport.infrastructure.controllers.auth;

import com.eraport.application.dto.shared.ApiResponse;
import com.eraport.application.dto.auth.LoginRequest;
import com.eraport.application.dto.auth.LoginResponse;
import com.eraport.application.services.auth.AuthService;
import com.eraport.shared.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractToken(request);
        UUID userId = jwtUtil.getUserIdFromToken(token);

        authService.logout(token, userId);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    @PostMapping("/revoke/{userId}")
    @Operation(summary = "Revoke user access")
    public ResponseEntity<ApiResponse<Void>> revokeUserAccess(
            @PathVariable UUID userId,
            HttpServletRequest request) {

        String token = extractToken(request);
        UUID revokedBy = jwtUtil.getUserIdFromToken(token);

        authService.revokeUserAccess(userId, revokedBy);
        return ResponseEntity.ok(ApiResponse.success("User access revoked", null));
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid token");
    }
}
