package com.newton.zaocycle.rider.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "riders")
@Getter
@Setter
@NoArgsConstructor
class RiderEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "assigned_ward", nullable = false, length = 50)
    private String assignedWard;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;
}
