package com.eraport.domain.services.users;

import com.eraport.domain.entities.User;
import com.eraport.domain.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(User user, UUID createdBy) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedBy(createdBy);
        return userRepository.save(user);
    }

    public User update(User user, UUID updatedBy) {
        user.setUpdatedBy(updatedBy);
        return userRepository.save(user);
    }

    public void delete(UUID id, UUID deletedBy) {
        User user = userRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.softDelete(deletedBy);
        userRepository.save(user);
    }

    public void revokeUser(UUID userId, UUID revokedBy) {
        User user = userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.revoke();
        user.setUpdatedBy(revokedBy);
        userRepository.save(user);
    }

    public void activateUser(UUID userId, UUID activatedBy) {
        User user = userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.activate();
        user.setUpdatedBy(activatedBy);
        userRepository.save(user);
    }
}
