package com.newton.zaocycle.buyer.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class Buyer {

    private final UUID id;
    private final String email;
    private String passwordHash;
    private final String phone;
    private final BuyerType buyerType;
    private String displayName;
    private String contactPerson;
    private String address;
    private String ward;
    private boolean active;
    private String profileImageUrl;
    private final Instant createdAt;
    private Instant updatedAt;

    public Buyer(UUID id, String email, String passwordHash, String phone,
                 BuyerType buyerType, String displayName, String contactPerson,
                 String address, String ward, boolean active,
                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.buyerType = buyerType;
        this.displayName = displayName;
        this.contactPerson = contactPerson;
        this.address = address;
        this.ward = ward;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Buyer create(String email, String passwordHash, String phone,
                               BuyerType buyerType, String displayName,
                               String contactPerson, String address, String ward) {
        Instant now = Instant.now();
        return new Buyer(IdGenerator.generate(), email, passwordHash, phone, buyerType,
                displayName, contactPerson, address, ward, true, now, now);
    }

    public void updateProfile(String displayName, String contactPerson, String address, String ward) {
        this.displayName = displayName;
        this.contactPerson = contactPerson;
        this.address = address;
        this.ward = ward;
        this.updatedAt = Instant.now();
    }

    public void updateProfileImage(String url) {
        this.profileImageUrl = url;
        this.updatedAt = Instant.now();
    }

    public UUID id()                  { return id; }
    public String email()             { return email; }
    public String passwordHash()      { return passwordHash; }
    public String phone()             { return phone; }
    public BuyerType buyerType()      { return buyerType; }
    public String displayName()       { return displayName; }
    public String contactPerson()     { return contactPerson; }
    public String address()           { return address; }
    public String ward()              { return ward; }
    public boolean isActive()         { return active; }
    public String profileImageUrl()   { return profileImageUrl; }
    public Instant createdAt()        { return createdAt; }
    public Instant updatedAt()        { return updatedAt; }
}
