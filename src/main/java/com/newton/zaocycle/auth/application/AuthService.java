package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.api.dto.TokenResponse;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.rider.domain.model.Rider;

public interface AuthService {
    TokenResponse loginUnified(String identifier, String credential);

    TokenResponse refresh(String refreshToken);

    void logout(String refreshToken);

    TokenResponse issueTokensForBuyer(Buyer buyer);

    TokenResponse issueTokensForRider(Rider rider);
}
