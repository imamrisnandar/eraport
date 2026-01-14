package com.eraport.application.services.auth;

import com.eraport.application.dto.auth.LoginRequest;
import com.eraport.application.dto.auth.LoginResponse;
import com.eraport.domain.entities.User;
import com.eraport.domain.services.loginhistory.LoginHistoryService;
import com.eraport.domain.services.users.UserCommandService;
import com.eraport.domain.services.users.UserQueryService;
import com.eraport.shared.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final LoginHistoryService loginHistoryService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userQueryService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.getIsActive() || user.getIsRevoked()) {
            throw new RuntimeException("User account is inactive or revoked");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        LocalDateTime tokenExpired = jwtUtil.getExpirationLocalDateTimeFromToken(token);

        // Create login history
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        loginHistoryService.createLoginHistory(
                user,
                ipAddress,
                userAgent,
                token,
                tokenExpired,
                user.getId());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .expiresAt(tokenExpired)
                .build();
    }

    public void logout(String token, UUID userId) {
        loginHistoryService.logout(token, userId);
    }

    public void revokeUserAccess(UUID userId, UUID revokedBy) {
        userCommandService.revokeUser(userId, revokedBy);
        loginHistoryService.revokeAllUserSessions(userId, revokedBy);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
