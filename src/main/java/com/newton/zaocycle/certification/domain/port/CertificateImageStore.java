package com.newton.zaocycle.certification.domain.port;

public interface CertificateImageStore {
    String upload(String filename, byte[] data, String contentType);
}
