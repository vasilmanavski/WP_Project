package com.churchevents.service.impl;

import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.repository.ConfirmationTokenRepository;
import com.churchevents.service.ConfirmationTokenService;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;


    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public ConfirmationToken findByConfirmationToken(String confirmationToken) {
        return this.confirmationTokenRepository.findByConfirmationToken(confirmationToken);
    }

    @Override
    public void save(ConfirmationToken confirmationToken) {
        this.confirmationTokenRepository.save(confirmationToken);
    }


}
