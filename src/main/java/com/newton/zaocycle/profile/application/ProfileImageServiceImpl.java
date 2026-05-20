package com.newton.zaocycle.profile.application;

import com.newton.zaocycle.auth.application.StaffService;
import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.profile.domain.port.ProfileImageStore;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
class ProfileImageServiceImpl implements ProfileImageService {

    private static final long MAX_BYTES = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final ProfileImageStore imageStore;
    private final FarmerService farmerService;
    private final RiderService riderService;
    private final BuyerService buyerService;
    private final StaffService staffService;

    ProfileImageServiceImpl(ProfileImageStore imageStore,
                            FarmerService farmerService,
                            RiderService riderService,
                            BuyerService buyerService,
                            StaffService staffService) {
        this.imageStore = imageStore;
        this.farmerService = farmerService;
        this.riderService = riderService;
        this.buyerService = buyerService;
        this.staffService = staffService;
    }

    @Override
    public String upload(AuthenticatedPrincipal principal, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ValidationException("Unsupported image type. Allowed: jpeg, png, webp");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new ValidationException("Image must be 5 MB or smaller");
        }

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new ValidationException("Failed to read uploaded file");
        }

        String extension = extensionFor(contentType);
        String key = "profiles/" + principal.role().name().toLowerCase()
                + "/" + principal.id() + "." + extension;

        String url = imageStore.upload(key, data, contentType);
        persistUrl(principal.id(), principal, url);
        return url;
    }

    @Override
    public String getImageUrl(AuthenticatedPrincipal principal) {
        UUID id = principal.id();
        return switch (principal.role()) {
            case FARMER -> farmerService.findById(id)
                    .map(f -> f.profileImageUrl()).orElse(null);
            case RIDER -> riderService.findById(id)
                    .map(r -> r.profileImageUrl()).orElse(null);
            case BUYER -> buyerService.findById(id)
                    .map(b -> b.profileImageUrl()).orElse(null);
            case ADMIN, COOP_MANAGER -> staffService.findById(id)
                    .map(s -> s.profileImageUrl()).orElse(null);
        };
    }

    private void persistUrl(UUID id, AuthenticatedPrincipal principal, String url) {
        switch (principal.role()) {
            case FARMER -> farmerService.updateProfileImage(id, url);
            case RIDER -> riderService.updateProfileImage(id, url);
            case BUYER -> buyerService.updateProfileImage(id, url);
            case ADMIN, COOP_MANAGER -> staffService.updateProfileImage(id, url);
        }
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }
}
