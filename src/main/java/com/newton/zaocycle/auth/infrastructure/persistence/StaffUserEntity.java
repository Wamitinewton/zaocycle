package com.newton.zaocycle.auth.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "staff_users")
@Getter
@Setter
@NoArgsConstructor
class StaffUserEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false)
    private boolean active;
}
