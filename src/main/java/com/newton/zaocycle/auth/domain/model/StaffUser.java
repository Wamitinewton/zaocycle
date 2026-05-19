package com.newton.zaocycle.auth.domain.model;

import com.newton.zaocycle.shared.exception.ValidationException;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class StaffUser {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private final Role role;
    private boolean active;
    private String profileImageUrl;
    private final Instant createdAt;
    private Instant updatedAt;

    public StaffUser(UUID id, String email, String passwordHash, String fullName,
                     Role role, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static StaffUser create(String email, String passwordHash, String fullName, Role role) {
        Instant now = Instant.now();
        return new StaffUser(IdGenerator.generate(), email, passwordHash, fullName, role, true, now, now);
    }

    public void ensureActive() {
        if (!active) {
            throw new ValidationException("Staff account is inactive");
        }
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void reactivate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void updateProfileImage(String url) {
        this.profileImageUrl = url;
        this.updatedAt = Instant.now();
    }

    public UUID id()                  { return id; }
    public String email()             { return email; }
    public String passwordHash()      { return passwordHash; }
    public String fullName()          { return fullName; }
    public Role role()                { return role; }
    public boolean isActive()         { return active; }
    public String profileImageUrl()   { return profileImageUrl; }
    public Instant createdAt()        { return createdAt; }
    public Instant updatedAt()        { return updatedAt; }
}
