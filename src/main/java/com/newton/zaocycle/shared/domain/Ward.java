package com.newton.zaocycle.shared.domain;

import com.newton.zaocycle.shared.exception.ValidationException;

public enum Ward {
    MWEA("Mwea"),
    GICHUGU("Gichugu"),
    KIRINYAGA_CENTRAL("Kirinyaga Central"),
    NDIA("Ndia");

    private final String displayName;

    Ward(String displayName) {
        this.displayName = displayName;
    }

    public static Ward fromIndex(int oneBasedIndex) {
        Ward[] values = values();
        if (oneBasedIndex < 1 || oneBasedIndex > values.length) {
            throw new ValidationException("Invalid ward selection: " + oneBasedIndex);
        }
        return values[oneBasedIndex - 1];
    }

    public String displayName() {
        return displayName;
    }
}
