package com.newton.zaocycle.farmer.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "farmers")
@Getter
@Setter
@NoArgsConstructor
class FarmerEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "full_name")
    private String fullName;

    @Column(length = 50)
    private String ward;

    @Column(name = "pin_hash")
    private String pinHash;

    @Column(name = "registration_complete", nullable = false)
    private boolean registrationComplete;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;
}
