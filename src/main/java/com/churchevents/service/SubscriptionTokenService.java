package com.churchevents.service;

import com.churchevents.model.Subscriber;
import com.churchevents.model.tokens.SubscriptionToken;

import java.util.Optional;

public interface SubscriptionTokenService {
    SubscriptionToken findBySubscriptionToken(String subscriptionToken);
    void save(SubscriptionToken subscriptionToken);
    SubscriptionToken findBySubscriber(Subscriber subscriber);
    Boolean checkIfTokenExists(String token);
}
