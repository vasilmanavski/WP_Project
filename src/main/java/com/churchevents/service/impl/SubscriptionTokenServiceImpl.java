package com.churchevents.service.impl;

import com.churchevents.model.Subscriber;
import com.churchevents.model.exceptions.InvalidArgumentsException;
import com.churchevents.model.exceptions.TokenAlreadyExists;
import com.churchevents.model.tokens.SubscriptionToken;
import com.churchevents.repository.SubscribersRepository;
import com.churchevents.repository.SubscriptionTokenRepository;
import com.churchevents.service.SubscriptionTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionTokenServiceImpl implements SubscriptionTokenService {

    private final SubscriptionTokenRepository subscriptionTokenRepository;

    public SubscriptionTokenServiceImpl(SubscriptionTokenRepository subscriptionTokenRepository) {
        this.subscriptionTokenRepository = subscriptionTokenRepository;
    }

    @Override
    public SubscriptionToken findBySubscriptionToken(String subscriptionToken) {
        return this.subscriptionTokenRepository.findBySubscriptionToken(subscriptionToken);
    }

    @Override
    public void save(SubscriptionToken subscriptionToken) {
        SubscriptionToken checkToken = findBySubscriptionToken(subscriptionToken.getSubscriptionToken());
        if (checkToken != null) throw new TokenAlreadyExists();
        this.subscriptionTokenRepository.save(subscriptionToken);
    }

    @Override
    public SubscriptionToken findBySubscriber(Subscriber subscriber) {
        if(subscriber == null) throw new InvalidArgumentsException();

        return this.subscriptionTokenRepository.findBySubscriber(subscriber);

    }

    @Override
    public Boolean checkIfTokenExists(String token) {
        return this.subscriptionTokenRepository.existsBySubscriptionToken(token);
    }


}
