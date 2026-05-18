package com.newton.zaocycle.pesticide.domain.port;

import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;

import java.util.List;
import java.util.UUID;

public interface PesticideApplicationRepository {
    PesticideApplication save(PesticideApplication application);
    List<PesticideApplication> findByFarmerId(UUID farmerId);
    List<PesticideApplication> findPendingByFarmerId(UUID farmerId);
}
