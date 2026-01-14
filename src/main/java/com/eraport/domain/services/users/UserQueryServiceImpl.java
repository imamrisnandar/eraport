package com.eraport.domain.services.users;

import com.eraport.domain.entities.User;
import com.eraport.domain.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameAndIsDeletedFalse(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndIsDeletedFalse(email);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findByIdAndNotDeleted(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndIsDeletedFalse(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndIsDeletedFalse(email);
    }
}
