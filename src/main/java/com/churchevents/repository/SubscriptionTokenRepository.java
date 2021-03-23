package com.churchevents.repository;

import com.churchevents.model.Subscriber;
import com.churchevents.model.tokens.SubscriptionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionTokenRepository extends JpaRepository<SubscriptionToken, String> {
    SubscriptionToken findBySubscriptionToken(String token);
    SubscriptionToken findBySubscriber(Subscriber subscriber);

    boolean existsBySubscriptionToken(String token);
}
