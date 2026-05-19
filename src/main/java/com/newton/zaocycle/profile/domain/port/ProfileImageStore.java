package com.newton.zaocycle.profile.domain.port;

public interface ProfileImageStore {
    String upload(String filename, byte[] data, String contentType);
}
