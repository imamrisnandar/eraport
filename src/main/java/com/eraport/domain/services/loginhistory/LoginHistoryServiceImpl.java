package com.eraport.domain.services.loginhistory;

import com.eraport.domain.entities.LoginHistory;
import com.eraport.domain.entities.User;
import com.eraport.domain.repositories.loginhistory.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistory createLoginHistory(User user, String ipAddress, String userAgent,
            String token, LocalDateTime tokenExpired,
            UUID createdBy) {
        LoginHistory loginHistory = LoginHistory.builder()
                .user(user)
                .loginTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .isSuccess(true)
                .token(token)
                .tokenExpired(tokenExpired)
                .isLoggedOut(false)
                .build();

        loginHistory.setCreatedBy(createdBy);
        return loginHistoryRepository.save(loginHistory);
    }

    public void logout(String token, UUID updatedBy) {
        Optional<LoginHistory> loginHistoryOpt = loginHistoryRepository.findByTokenAndIsDeletedFalse(token);
        if (loginHistoryOpt.isPresent()) {
            LoginHistory loginHistory = loginHistoryOpt.get();
            loginHistory.setLogoutTime(LocalDateTime.now());
            loginHistory.setIsLoggedOut(true);
            loginHistory.setUpdatedBy(updatedBy);
            loginHistoryRepository.save(loginHistory);
        }
    }

    public List<LoginHistory> getUserLoginHistory(UUID userId) {
        return loginHistoryRepository.findByUserIdAndIsDeletedFalseOrderByLoginTimeDesc(userId);
    }

    public List<LoginHistory> getActiveSessions(UUID userId) {
        return loginHistoryRepository.findActiveSessionsByUserId(userId);
    }

    public void revokeAllUserSessions(UUID userId, UUID revokedBy) {
        List<LoginHistory> activeSessions = getActiveSessions(userId);
        for (LoginHistory session : activeSessions) {
            session.setIsLoggedOut(true);
            session.setLogoutTime(LocalDateTime.now());
            session.setUpdatedBy(revokedBy);
        }
        loginHistoryRepository.saveAll(activeSessions);
    }
}
