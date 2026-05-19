package com.newton.zaocycle.certification.application;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TokenGenerator {

    // base32 alphabet excluding visually confusing characters: 0, O, I, 1
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TOKEN_CHARS = 8;
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        char[] chars = new char[TOKEN_CHARS];
        for (int i = 0; i < TOKEN_CHARS; i++) {
            chars[i] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
        }
        return "ZC-" + new String(chars, 0, 4) + "-" + new String(chars, 4, 4);
    }
}
