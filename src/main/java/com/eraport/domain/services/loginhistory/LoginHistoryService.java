package com.eraport.domain.services.loginhistory;

import com.eraport.domain.entities.LoginHistory;
import com.eraport.domain.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoginHistoryService {
    LoginHistory createLoginHistory(User user, String ipAddress, String userAgent,
            String token, LocalDateTime tokenExpired,
            UUID createdBy);

    void logout(String token, UUID updatedBy);

    List<LoginHistory> getUserLoginHistory(UUID userId);

    List<LoginHistory> getActiveSessions(UUID userId);

    void revokeAllUserSessions(UUID userId, UUID revokedBy);
}
