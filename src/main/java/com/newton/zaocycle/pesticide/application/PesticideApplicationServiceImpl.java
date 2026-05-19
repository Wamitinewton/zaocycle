package com.newton.zaocycle.pesticide.application;

import com.newton.zaocycle.chemical.application.ChemicalService;
import com.newton.zaocycle.chemical.domain.model.Chemical;
import com.newton.zaocycle.pesticide.application.command.LogPesticideApplicationCommand;
import com.newton.zaocycle.pesticide.application.event.PesticideApplicationLogged;
import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.pesticide.domain.port.PesticideApplicationRepository;
import com.newton.zaocycle.pesticide.domain.service.SafeHarvestCalculator;
import com.newton.zaocycle.shared.exception.NotFoundException;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
class PesticideApplicationServiceImpl implements PesticideApplicationService {

    private final PesticideApplicationRepository repository;
    private final ChemicalService chemicalService;
    private final SafeHarvestCalculator calculator;
    private final ApplicationEventPublisher events;

    PesticideApplicationServiceImpl(PesticideApplicationRepository repository,
                                    ChemicalService chemicalService,
                                    SafeHarvestCalculator calculator,
                                    ApplicationEventPublisher events) {
        this.repository = repository;
        this.chemicalService = chemicalService;
        this.calculator = calculator;
        this.events = events;
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
        PesticideApplication saved = repository.save(application);

        events.publishEvent(new PesticideApplicationLogged(
                saved.id(), saved.farmerId(), saved.crop(),
                saved.chemicalId(), saved.safeHarvestDate(), saved.appliedAt()
        ));

        return saved;
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

    @Override
    public PesticideApplication markSafe(UUID applicationId) {
        PesticideApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Pesticide application not found: " + applicationId));
        application.markSafe();
        return repository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public PesticideApplication findById(UUID applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Pesticide application not found: " + applicationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PesticideApplication> findRecentByFarmer(UUID farmerId, int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        return repository.findByFarmerIdAndAppliedAtAfter(farmerId, since);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PesticideApplication> findReadyForPickup(UUID farmerId) {
        return repository.findByFarmerIdAndStatus(farmerId, ApplicationStatus.SAFE);
    }
}
