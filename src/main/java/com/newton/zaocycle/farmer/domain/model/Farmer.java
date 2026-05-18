package com.newton.zaocycle.farmer.domain.model;

import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class Farmer {

    private final UUID id;
    private final PhoneNumber phone;
    private String fullName;
    private Ward ward;
    private String pinHash;
    private boolean registrationComplete;
    private final Instant createdAt;
    private Instant updatedAt;

    public Farmer(UUID id, PhoneNumber phone, String fullName, Ward ward, String pinHash,
                  boolean registrationComplete, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.phone = phone;
        this.fullName = fullName;
        this.ward = ward;
        this.pinHash = pinHash;
        this.registrationComplete = registrationComplete;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Farmer newUnregistered(PhoneNumber phone) {
        Instant now = Instant.now();
        return new Farmer(IdGenerator.generate(), phone, null, null, null, false, now, now);
    }

    public void completeRegistration(String fullName, Ward ward, String pinHash) {
        if (this.registrationComplete) {
            throw new IllegalStateException("Farmer is already registered");
        }
        this.fullName = fullName;
        this.ward = ward;
        this.pinHash = pinHash;
        this.registrationComplete = true;
        this.updatedAt = Instant.now();
    }

    public UUID id()                        { return id; }
    public PhoneNumber phone()              { return phone; }
    public String fullName()                { return fullName; }
    public Ward ward()                      { return ward; }
    public String pinHash()                 { return pinHash; }
    public boolean isRegistrationComplete() { return registrationComplete; }
    public Instant createdAt()              { return createdAt; }
    public Instant updatedAt()              { return updatedAt; }
}
