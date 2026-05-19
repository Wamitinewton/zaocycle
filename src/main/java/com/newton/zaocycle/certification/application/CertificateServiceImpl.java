package com.newton.zaocycle.certification.application;

import com.newton.zaocycle.certification.application.event.CertificateIssued;
import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.certification.domain.port.CertificateImageStore;
import com.newton.zaocycle.certification.domain.port.CertificateRepository;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository repo;
    private final PesticideApplicationService pesticideService;
    private final TokenGenerator tokenGenerator;
    private final QrGenerator qrGenerator;
    private final CertificateImageStore imageStore;
    private final CertificationProperties certificationProperties;
    private final ApplicationEventPublisher events;

    CertificateServiceImpl(CertificateRepository repo,
                           PesticideApplicationService pesticideService,
                           TokenGenerator tokenGenerator,
                           QrGenerator qrGenerator,
                           CertificateImageStore imageStore,
                           CertificationProperties certificationProperties,
                           ApplicationEventPublisher events) {
        this.repo = repo;
        this.pesticideService = pesticideService;
        this.tokenGenerator = tokenGenerator;
        this.qrGenerator = qrGenerator;
        this.imageStore = imageStore;
        this.certificationProperties = certificationProperties;
        this.events = events;
    }

    @Override
    @Transactional
    public Certificate issueFor(UUID applicationId) {
        Optional<Certificate> existing = repo.findByApplicationId(applicationId);
        if (existing.isPresent()) return existing.get();

        PesticideApplication application = pesticideService.findById(applicationId);

        String token = tokenGenerator.generate();
        String verifyUrl = certificationProperties.verifyBaseUrl() + "/api/v1/certificates/verify/" + token;
        byte[] png = qrGenerator.generate(verifyUrl, 512);
        String imageUrl = imageStore.upload(token + ".png", png, "image/png");

        Instant expiresAt = LocalDate.now()
                .plusDays(certificationProperties.expiresAfterDays())
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Certificate cert = Certificate.create(applicationId, token, imageUrl, Instant.now(), expiresAt);
        repo.save(cert);

        events.publishEvent(new CertificateIssued(
                cert.id(), applicationId, application.farmerId(),
                application.crop(), token, imageUrl
        ));
        return cert;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Certificate> findById(UUID id) {
        return repo.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Certificate> findByToken(String token) {
        return repo.findByToken(token);
    }

    @Override
    @Transactional
    public void recordVerification(UUID certId) {
        Certificate cert = repo.findById(certId)
                .orElseThrow(() -> new NotFoundException("Certificate: " + certId));
        cert.recordVerification();
        repo.save(cert);
    }

    @Override
    @Transactional
    public Certificate revoke(UUID certId) {
        Certificate cert = repo.findById(certId)
                .orElseThrow(() -> new NotFoundException("Certificate: " + certId));
        cert.revoke();
        return repo.save(cert);
    }
}
