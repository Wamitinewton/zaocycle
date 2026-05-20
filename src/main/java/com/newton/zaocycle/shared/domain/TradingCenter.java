package com.newton.zaocycle.shared.domain;

import com.newton.zaocycle.shared.exception.ValidationException;

import java.util.Arrays;
import java.util.List;

public enum TradingCenter {

    // Mwea Ward
    MWEA_TOWN("Mwea Town", Ward.MWEA, -0.6584, 37.3968),
    WANGURU("Wanguru", Ward.MWEA, -0.6277, 37.4099),
    TEBERE("Tebere", Ward.MWEA, -0.6880, 37.4325),
    MUTITHI("Mutithi", Ward.MWEA, -0.7170, 37.3625),
    KANGAI("Kangai", Ward.MWEA, -0.6100, 37.3650),

    // Gichugu Ward
    KERUGOYA("Kerugoya", Ward.GICHUGU, -0.4993, 37.2768),
    KAGIO("Kagio", Ward.GICHUGU, -0.5150, 37.3300),
    KIANYAGA("Kianyaga", Ward.GICHUGU, -0.4500, 37.3550),
    NYAMINDI("Nyamindi", Ward.GICHUGU, -0.5700, 37.2900),

    // Kirinyaga Central Ward
    KUTUS("Kutus", Ward.KIRINYAGA_CENTRAL, -0.5330, 37.2650),
    SAGANA("Sagana", Ward.KIRINYAGA_CENTRAL, -0.6705, 37.2084),
    BARICHO("Baricho", Ward.KIRINYAGA_CENTRAL, -0.5700, 37.2500),
    KAMUTHE("Kamuthe", Ward.KIRINYAGA_CENTRAL, -0.5000, 37.2400),

    // Ndia Ward
    KARURUMO("Karurumo", Ward.NDIA, -0.4000, 37.3800),
    NGARIAMA("Ngariama", Ward.NDIA, -0.4230, 37.3150),
    GAICHANJIRU("Gaichanjiru", Ward.NDIA, -0.4560, 37.2600),
    KIRITI("Kiriti", Ward.NDIA, -0.3800, 37.3200);

    private final String displayName;
    private final Ward ward;
    private final double latitude;
    private final double longitude;

    TradingCenter(String displayName, Ward ward, double latitude, double longitude) {
        this.displayName = displayName;
        this.ward = ward;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<TradingCenter> forWard(Ward ward) {
        return Arrays.stream(values())
                .filter(tc -> tc.ward == ward)
                .toList();
    }

    public static TradingCenter fromIndex(Ward ward, int oneBasedIndex) {
        List<TradingCenter> centers = forWard(ward);
        if (oneBasedIndex < 1 || oneBasedIndex > centers.size()) {
            throw new ValidationException("Invalid trading centre selection: " + oneBasedIndex);
        }
        return centers.get(oneBasedIndex - 1);
    }

    public String displayName() {
        return displayName;
    }

    public Ward ward() {
        return ward;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }
}
