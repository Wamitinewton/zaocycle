package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.api.dto.TokenResponse;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.farmer.domain.model.Farmer;

public interface AuthService {
    TokenResponse loginFarmer(String phone, String pin);
    TokenResponse loginRider(String phone, String password);
    TokenResponse loginStaff(String email, String password);
    TokenResponse loginBuyer(String email, String password);
    TokenResponse refresh(String refreshToken);
    void logout(String refreshToken);
    TokenResponse issueTokensForBuyer(Buyer buyer);
    TokenResponse issueTokensForFarmer(Farmer farmer);
}
