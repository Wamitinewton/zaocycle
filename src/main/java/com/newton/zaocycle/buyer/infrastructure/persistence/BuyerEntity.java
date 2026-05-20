package com.newton.zaocycle.buyer.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "buyers")
@Getter
@Setter
@NoArgsConstructor
class BuyerEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(name = "buyer_type", nullable = false, length = 20)
    private String buyerType;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String ward;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;
}
