package com.newton.zaocycle.certification.application;

public interface QrGenerator {
    byte[] generate(String content, int sizePixels);
}
