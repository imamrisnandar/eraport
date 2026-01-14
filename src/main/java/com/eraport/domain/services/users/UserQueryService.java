package com.eraport.domain.services.users;

import com.eraport.domain.entities.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserQueryService {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    List<User> findAll();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
