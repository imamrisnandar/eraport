package com.eraport.application.services.auth;

import com.eraport.application.dto.auth.LoginRequest;
import com.eraport.application.dto.auth.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

    void logout(String token, UUID userId);

    void revokeUserAccess(UUID userId, UUID revokedBy);
}
