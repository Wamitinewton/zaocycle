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
    private String tradingCenter;
    private Double latitude;
    private Double longitude;
    private String profileImageUrl;
    private final Instant createdAt;
    private Instant updatedAt;

    public Farmer(UUID id, PhoneNumber phone, String fullName, Ward ward, String pinHash,
                  boolean registrationComplete, String tradingCenter, Double latitude, Double longitude,
                  String profileImageUrl, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.phone = phone;
        this.fullName = fullName;
        this.ward = ward;
        this.pinHash = pinHash;
        this.registrationComplete = registrationComplete;
        this.tradingCenter = tradingCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Farmer newUnregistered(PhoneNumber phone) {
        Instant now = Instant.now();
        return new Farmer(IdGenerator.generate(), phone, null, null, null,
                false, null, null, null, null, now, now);
    }

    public void completeRegistration(String fullName, Ward ward, String pinHash,
                                     String tradingCenter, Double latitude, Double longitude) {
        if (this.registrationComplete) {
            throw new IllegalStateException("Farmer is already registered");
        }
        this.fullName = fullName;
        this.ward = ward;
        this.pinHash = pinHash;
        this.tradingCenter = tradingCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.registrationComplete = true;
        this.updatedAt = Instant.now();
    }

    public void updateLocation(String tradingCenter, Double latitude, Double longitude) {
        this.tradingCenter = tradingCenter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = Instant.now();
    }

    public void updateProfileImage(String url) {
        this.profileImageUrl = url;
        this.updatedAt = Instant.now();
    }

    public UUID id()                        { return id; }
    public PhoneNumber phone()              { return phone; }
    public String fullName()                { return fullName; }
    public Ward ward()                      { return ward; }
    public String pinHash()                 { return pinHash; }
    public boolean isRegistrationComplete() { return registrationComplete; }
    public String tradingCenter()           { return tradingCenter; }
    public Double latitude()                { return latitude; }
    public Double longitude()               { return longitude; }
    public String profileImageUrl()         { return profileImageUrl; }
    public Instant createdAt()              { return createdAt; }
    public Instant updatedAt()              { return updatedAt; }
}
