package com.newton.zaocycle.certification.application;

import com.newton.zaocycle.certification.application.event.CertificateIssued;
import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.certification.domain.model.CertificateStatus;
import com.newton.zaocycle.certification.domain.port.CertificateImageStore;
import com.newton.zaocycle.certification.domain.port.CertificateRepository;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {

    private static final UUID APPLICATION_ID = UUID.randomUUID();
    private static final UUID FARMER_ID = UUID.randomUUID();
    private static final UUID CHEMICAL_ID = UUID.randomUUID();
    @Mock
    private CertificateRepository repo;
    @Mock
    private PesticideApplicationService pesticideService;
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private QrGenerator qrGenerator;
    @Mock
    private CertificateImageStore imageStore;
    @Mock
    private ApplicationEventPublisher events;
    private CertificateServiceImpl service;

    @BeforeEach
    void setUp() {
        CertificationProperties props = new CertificationProperties("https://zaocycle.app", 14);
        service = new CertificateServiceImpl(repo, pesticideService, tokenGenerator, qrGenerator, imageStore, props, events);
    }

    @Test
    void issueFor_createsAndPersistsCertificate() {
        when(repo.findByApplicationId(APPLICATION_ID)).thenReturn(Optional.empty());
        when(pesticideService.findById(APPLICATION_ID)).thenReturn(stubApplication());
        when(tokenGenerator.generate()).thenReturn("ZC-ABCD-EFGH");
        when(qrGenerator.generate(any(), eq(512))).thenReturn(new byte[]{1, 2, 3});
        when(imageStore.upload(any(), any(), any())).thenReturn("https://example.com/ZC-ABCD-EFGH.png");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Certificate cert = service.issueFor(APPLICATION_ID);

        assertThat(cert.token()).isEqualTo("ZC-ABCD-EFGH");
        assertThat(cert.status()).isEqualTo(CertificateStatus.ACTIVE);
        verify(repo).save(any(Certificate.class));
    }

    @Test
    void issueFor_publishesCertificateIssuedEvent() {
        when(repo.findByApplicationId(APPLICATION_ID)).thenReturn(Optional.empty());
        when(pesticideService.findById(APPLICATION_ID)).thenReturn(stubApplication());
        when(tokenGenerator.generate()).thenReturn("ZC-ABCD-EFGH");
        when(qrGenerator.generate(any(), eq(512))).thenReturn(new byte[]{1});
        when(imageStore.upload(any(), any(), any())).thenReturn("https://example.com/img.png");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.issueFor(APPLICATION_ID);

        ArgumentCaptor<CertificateIssued> captor = ArgumentCaptor.forClass(CertificateIssued.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().applicationId()).isEqualTo(APPLICATION_ID);
        assertThat(captor.getValue().farmerId()).isEqualTo(FARMER_ID);
    }

    @Test
    void issueFor_isIdempotent_returnsExistingCertificate() {
        Certificate existing = Certificate.create(APPLICATION_ID, "ZC-XXXX-YYYY",
                "https://example.com/qr.png", Instant.now(), Instant.now().plusSeconds(86400));
        when(repo.findByApplicationId(APPLICATION_ID)).thenReturn(Optional.of(existing));

        Certificate result = service.issueFor(APPLICATION_ID);

        assertThat(result.token()).isEqualTo("ZC-XXXX-YYYY");
        verify(pesticideService, never()).findById(any());
        verify(events, never()).publishEvent(any());
    }

    private PesticideApplication stubApplication() {
        return new PesticideApplication(APPLICATION_ID, FARMER_ID, CHEMICAL_ID, "Maize",
                50.0, Instant.now(), LocalDate.now().plusDays(7),
                ApplicationStatus.PENDING, "USSD", Instant.now(), Instant.now());
    }
}
