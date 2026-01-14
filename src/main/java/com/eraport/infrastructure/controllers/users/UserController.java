package com.eraport.infrastructure.controllers.users;

import com.eraport.application.dto.shared.ApiResponse;
import com.eraport.application.dto.users.UserRequest;
import com.eraport.application.dto.users.UserResponse;
import com.eraport.domain.entities.User;
import com.eraport.domain.services.users.UserCommandService;
import com.eraport.domain.services.users.UserQueryService;
import com.eraport.shared.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userQueryService.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        User user = userQueryService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(ApiResponse.success(mapToUserResponse(user)));
    }

    @PostMapping
    @Operation(summary = "Create new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest) {

        if (userQueryService.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userQueryService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String token = extractToken(httpRequest);
        UUID createdBy = jwtUtil.getUserIdFromToken(token);

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .build();

        User savedUser = userCommandService.create(user, createdBy);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", mapToUserResponse(savedUser)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request,
            HttpServletRequest httpRequest) {

        User user = userQueryService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = extractToken(httpRequest);
        UUID updatedBy = jwtUtil.getUserIdFromToken(token);

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }

        User updatedUser = userCommandService.update(user, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", mapToUserResponse(updatedUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {

        String token = extractToken(httpRequest);
        UUID deletedBy = jwtUtil.getUserIdFromToken(token);

        userCommandService.delete(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate user")
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @PathVariable UUID id,
            HttpServletRequest httpRequest) {

        String token = extractToken(httpRequest);
        UUID activatedBy = jwtUtil.getUserIdFromToken(token);

        userCommandService.activateUser(id, activatedBy);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", null));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .isRevoked(user.getIsRevoked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Invalid token");
    }
}
