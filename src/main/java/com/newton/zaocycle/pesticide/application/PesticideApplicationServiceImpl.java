package com.newton.zaocycle.pesticide.application;

import com.newton.zaocycle.chemical.application.ChemicalService;
import com.newton.zaocycle.chemical.domain.model.Chemical;
import com.newton.zaocycle.pesticide.application.command.LogPesticideApplicationCommand;
import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.pesticide.domain.port.PesticideApplicationRepository;
import com.newton.zaocycle.pesticide.domain.service.SafeHarvestCalculator;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
class PesticideApplicationServiceImpl implements PesticideApplicationService {

    private final PesticideApplicationRepository repository;
    private final ChemicalService chemicalService;
    private final SafeHarvestCalculator calculator;

    PesticideApplicationServiceImpl(PesticideApplicationRepository repository,
                                     ChemicalService chemicalService,
                                     SafeHarvestCalculator calculator) {
        this.repository = repository;
        this.chemicalService = chemicalService;
        this.calculator = calculator;
    }

    @Override
    public PesticideApplication log(LogPesticideApplicationCommand command) {
        Chemical chemical = chemicalService.getById(command.chemicalId());
        LocalDate safeDate = calculator.computeSafeHarvestDate(chemical);
        Instant now = Instant.now();

        PesticideApplication application = new PesticideApplication(
                IdGenerator.generate(),
                command.farmerId(),
                command.chemicalId(),
                command.crop(),
                command.quantityMl(),
                now,
                safeDate,
                ApplicationStatus.PENDING,
                "USSD",
                now,
                now
        );
        return repository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PesticideApplication> getByFarmer(UUID farmerId) {
        return repository.findByFarmerId(farmerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PesticideApplication> getPendingByFarmer(UUID farmerId) {
        return repository.findPendingByFarmerId(farmerId);
    }
}
