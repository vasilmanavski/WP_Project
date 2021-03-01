package com.churchevents.service;

import com.churchevents.model.tokens.ConfirmationToken;

public interface ConfirmationTokenService {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
    void save(ConfirmationToken confirmationToken);
}
