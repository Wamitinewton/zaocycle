package com.newton.zaocycle.pesticide.application;

import com.newton.zaocycle.pesticide.application.command.LogPesticideApplicationCommand;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;

import java.util.List;
import java.util.UUID;

public interface PesticideApplicationService {
    PesticideApplication log(LogPesticideApplicationCommand command);
    List<PesticideApplication> getByFarmer(UUID farmerId);
    List<PesticideApplication> getPendingByFarmer(UUID farmerId);
}
