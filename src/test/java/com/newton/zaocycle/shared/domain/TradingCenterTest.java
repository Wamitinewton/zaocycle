package com.newton.zaocycle.shared.domain;

import com.newton.zaocycle.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class TradingCenterTest {

    @ParameterizedTest
    @EnumSource(Ward.class)
    void forWard_returnsOnlyCentersForThatWard(Ward ward) {
        List<TradingCenter> centers = TradingCenter.forWard(ward);

        assertThat(centers).isNotEmpty();
        assertThat(centers).allMatch(tc -> tc.ward() == ward);
    }

    @Test
    void forWard_mwea_hasFiveCenters() {
        assertThat(TradingCenter.forWard(Ward.MWEA)).hasSize(5);
    }

    @Test
    void forWard_gichugu_hasFourCenters() {
        assertThat(TradingCenter.forWard(Ward.GICHUGU)).hasSize(4);
    }

    @Test
    void forWard_kirinyagaCentral_hasFourCenters() {
        assertThat(TradingCenter.forWard(Ward.KIRINYAGA_CENTRAL)).hasSize(4);
    }

    @Test
    void forWard_ndia_hasFourCenters() {
        assertThat(TradingCenter.forWard(Ward.NDIA)).hasSize(4);
    }

    @Test
    void fromIndex_firstEntry_returnsFirstCenter() {
        TradingCenter center = TradingCenter.fromIndex(Ward.MWEA, 1);
        assertThat(center.ward()).isEqualTo(Ward.MWEA);
        assertThat(center.displayName()).isEqualTo("Mwea Town");
    }

    @Test
    void fromIndex_lastEntry_returnsLastCenter() {
        List<TradingCenter> centers = TradingCenter.forWard(Ward.NDIA);
        TradingCenter last = TradingCenter.fromIndex(Ward.NDIA, centers.size());
        assertThat(last).isEqualTo(centers.get(centers.size() - 1));
    }

    @Test
    void fromIndex_zeroIndex_throwsValidationException() {
        assertThatThrownBy(() -> TradingCenter.fromIndex(Ward.MWEA, 0))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void fromIndex_outOfRange_throwsValidationException() {
        int outOfRange = TradingCenter.forWard(Ward.MWEA).size() + 1;
        assertThatThrownBy(() -> TradingCenter.fromIndex(Ward.MWEA, outOfRange))
                .isInstanceOf(ValidationException.class);
    }

    @ParameterizedTest
    @EnumSource(TradingCenter.class)
    void allCenters_haveValidCoordinates(TradingCenter center) {
        assertThat(center.latitude()).isBetween(-90.0, 90.0);
        assertThat(center.longitude()).isBetween(-180.0, 180.0);
        assertThat(center.displayName()).isNotBlank();
        assertThat(center.ward()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(TradingCenter.class)
    void allCenters_coordinatesAreInKirinyagaCounty(TradingCenter center) {
        // Kirinyaga County bounding box (approximate)
        assertThat(center.latitude()).isBetween(-0.80, -0.35);
        assertThat(center.longitude()).isBetween(37.10, 37.55);
    }
}
