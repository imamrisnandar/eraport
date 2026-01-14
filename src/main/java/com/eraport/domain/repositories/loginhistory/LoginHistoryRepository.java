package com.eraport.domain.repositories.loginhistory;

import com.eraport.domain.entities.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {

    List<LoginHistory> findByUserIdAndIsDeletedFalseOrderByLoginTimeDesc(UUID userId);

    Optional<LoginHistory> findByTokenAndIsDeletedFalse(String token);

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.isLoggedOut = false AND lh.isDeleted = false")
    List<LoginHistory> findActiveSessionsByUserId(UUID userId);
}
