package com.newton.zaocycle.profile.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.profile.api.dto.ProfileImageResponse;
import com.newton.zaocycle.profile.application.ProfileImageService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileImageService profileImageService;

    public ProfileController(ProfileImageService profileImageService) {
        this.profileImageService = profileImageService;
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileImageResponse uploadImage(
            @AuthenticationPrincipal AuthenticatedPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        String url = profileImageService.upload(principal, file);
        return new ProfileImageResponse(url);
    }

    @GetMapping("/image")
    public ProfileImageResponse getImage(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return new ProfileImageResponse(profileImageService.getImageUrl(principal));
    }
}
