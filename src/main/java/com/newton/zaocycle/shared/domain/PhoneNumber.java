package com.newton.zaocycle.shared.domain;

import com.newton.zaocycle.shared.exception.ValidationException;

public final class PhoneNumber {

    private static final String E164_PATTERN = "^\\+[1-9]\\d{7,14}$";

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    public static PhoneNumber of(String raw) {
        if (raw == null || !raw.matches(E164_PATTERN)) {
            throw new ValidationException("Invalid phone number: " + raw);
        }
        return new PhoneNumber(raw);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber p)) return false;
        return value.equals(p.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
