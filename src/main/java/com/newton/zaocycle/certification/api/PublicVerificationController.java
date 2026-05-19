package com.newton.zaocycle.certification.api;

import com.newton.zaocycle.certification.api.dto.PublicCertificateResponse;
import com.newton.zaocycle.certification.application.CertificateService;
import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.chemical.application.ChemicalService;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/certificates")
public class PublicVerificationController {

    private final CertificateService certService;
    private final PesticideApplicationService pesticideService;
    private final ChemicalService chemicalService;
    private final FarmerService farmerService;

    public PublicVerificationController(CertificateService certService,
                                         PesticideApplicationService pesticideService,
                                         ChemicalService chemicalService,
                                         FarmerService farmerService) {
        this.certService = certService;
        this.pesticideService = pesticideService;
        this.chemicalService = chemicalService;
        this.farmerService = farmerService;
    }

    @GetMapping("/verify/{token}")
    @Transactional
    public PublicCertificateResponse verify(@PathVariable String token) {
        Certificate cert = certService.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Certificate not found"));
        certService.recordVerification(cert.id());

        PesticideApplication application = pesticideService.findById(cert.applicationId());
        String chemicalName = chemicalService.getById(application.chemicalId()).name();
        Farmer farmer = farmerService.findById(application.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer: " + application.farmerId()));
        String ward = farmer.ward() != null ? farmer.ward().displayName() : "";

        return PublicCertificateResponse.from(cert, application, chemicalName, farmer.fullName(), ward);
    }
}
