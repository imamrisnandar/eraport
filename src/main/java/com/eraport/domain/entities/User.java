package com.eraport.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_revoked")
    private Boolean isRevoked = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoginHistory> loginHistories = new ArrayList<>();

    public void revoke() {
        this.isRevoked = true;
        this.isActive = false;
    }

    public void activate() {
        this.isRevoked = false;
        this.isActive = true;
    }
}
