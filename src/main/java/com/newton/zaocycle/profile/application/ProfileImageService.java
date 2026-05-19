package com.newton.zaocycle.profile.application;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {
    String upload(AuthenticatedPrincipal principal, MultipartFile file);
    String getImageUrl(AuthenticatedPrincipal principal);
}
