package com.newton.zaocycle.collection.domain.port;

public interface PickupPhotoStore {
    String upload(String filename, byte[] data, String contentType);
}
