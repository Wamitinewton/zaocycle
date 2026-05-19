package com.newton.zaocycle.pesticide.domain.port;

import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PesticideApplicationRepository {
    PesticideApplication save(PesticideApplication application);
    Optional<PesticideApplication> findById(UUID id);
    List<PesticideApplication> findByFarmerId(UUID farmerId);
    List<PesticideApplication> findPendingByFarmerId(UUID farmerId);
    List<PesticideApplication> findByFarmerIdAndAppliedAtAfter(UUID farmerId, Instant since);
    List<PesticideApplication> findByFarmerIdAndStatus(UUID farmerId, ApplicationStatus status);
}
