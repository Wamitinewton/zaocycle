package com.newton.zaocycle.certification.application.listener;

import com.newton.zaocycle.certification.application.CertificateService;
import com.newton.zaocycle.pesticide.application.event.ApplicationMatured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ApplicationMaturedListener {

    private final CertificateService certificateService;

    public ApplicationMaturedListener(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @TransactionalEventListener
    public void on(ApplicationMatured event) {
        certificateService.issueFor(event.applicationId());
    }
}
