package com.churchevents.service.impl;

import com.churchevents.model.Subscriber;
import com.churchevents.model.events.SubscriberEmailSentEvent;
import com.churchevents.model.exceptions.*;
import com.churchevents.model.tokens.SubscriptionToken;
import com.churchevents.repository.SubscribersRepository;
import com.churchevents.repository.SubscriptionTokenRepository;
import com.churchevents.service.SubscribersService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class SubscribersServiceImpl implements SubscribersService {

    private final SubscribersRepository subscribersRepository;
    private final SubscriptionTokenRepository subscriptionTokenRepository;

    public SubscribersServiceImpl(SubscribersRepository subscribersRepository, SubscriptionTokenRepository subscriptionToken) {
        this.subscribersRepository = subscribersRepository;
        this.subscriptionTokenRepository = subscriptionToken;
    }

    @Override
    public List<Subscriber> findAll() {
        return this.subscribersRepository.findAll();
    }

    @Override
    public void save(String email, Boolean isEnabled) {
        if(email.isEmpty() || email == null) throw new InvalidEmailOrPasswordException();
        if(subscribersRepository.existsByEmail(email) && isEnabled == false) throw new EmailAlreadyExistsException(email);
        Subscriber subscriber = new Subscriber(email, isEnabled);
        this.subscribersRepository.save(subscriber);

    }

    @Override
    public void timerForVerification(SubscriberEmailSentEvent event) {
        CompletableFuture.delayedExecutor(120, TimeUnit.SECONDS).execute(() -> {

            Subscriber subscriber = this.subscribersRepository.findByEmail(event.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException(event.getEmail()));

            SubscriptionToken subscriptionToken = this.subscriptionTokenRepository.findBySubscriber(subscriber);
            if(subscriptionTokenRepository.existsBySubscriptionToken(subscriptionToken.getSubscriptionToken())) {
                this.subscriptionTokenRepository.delete(subscriptionToken);
            }

            if (!subscriber.getIsEnabled()) {
                this.subscribersRepository.delete(subscriber);
            }
        });
    }

    @Override
    public void delete(Subscriber subscriber) {
        if(subscriber == null) throw new InvalidArgumentsException();

        this.subscribersRepository.delete(subscriber);
    }

    @Override
    public void deleteTokenAndSubscriber(SubscriptionToken token, Subscriber subscriber) {
        if(token == null || subscriber == null) throw new InvalidArgumentsException();

        this.subscriptionTokenRepository.delete(token);
        this.subscribersRepository.delete(subscriber);
    }

    @Override
    public Optional<Subscriber> findByEmail(String email) {
        return this.subscribersRepository.findByEmail(email);
    }
}
