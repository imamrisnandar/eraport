package com.eraport.infrastructure.config;

import com.eraport.domain.entities.User;
import com.eraport.domain.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String username = "administrator";
        String email = "admin@mail.com";

        Optional<User> userByUsername = userRepository.findByUsername(username);
        Optional<User> userByEmail = userRepository.findByEmail(email);

        if (userByUsername.isEmpty() && userByEmail.isEmpty()) {
            User admin = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode("password123"))
                    .fullName("Administrator")
                    .isActive(true)
                    .isRevoked(false)
                    .build();

            userRepository.save(admin);
            log.info("Admin user created successfully.");
        } else {
            log.info("Admin user already exists. Skipping creation.");
        }
    }
}
