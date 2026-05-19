package com.newton.zaocycle.rider.domain.model;

import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class Rider {

    private final UUID id;
    private final PhoneNumber phone;
    private final String fullName;
    private String passwordHash;
    private final Ward assignedWard;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    public Rider(UUID id, PhoneNumber phone, String fullName, String passwordHash,
                 Ward assignedWard, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.phone = phone;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.assignedWard = assignedWard;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Rider create(PhoneNumber phone, String fullName, Ward ward, String passwordHash) {
        Instant now = Instant.now();
        return new Rider(IdGenerator.generate(), phone, fullName, passwordHash, ward, true, now, now);
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void changePassword(String newHash) {
        this.passwordHash = newHash;
        this.updatedAt = Instant.now();
    }

    public UUID id()              { return id; }
    public PhoneNumber phone()    { return phone; }
    public String fullName()      { return fullName; }
    public String passwordHash()  { return passwordHash; }
    public Ward assignedWard()    { return assignedWard; }
    public boolean isActive()     { return active; }
    public Instant createdAt()    { return createdAt; }
    public Instant updatedAt()    { return updatedAt; }
}
