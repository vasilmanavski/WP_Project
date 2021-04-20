package com.churchevents.repository;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.model.tokens.SubscriptionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionTokenRepository extends JpaRepository<SubscriptionToken, String> {
    SubscriptionToken findBySubscriptionToken(String token);

    @Query(value = "SELECT ct FROM SubscriptionToken ct JOIN Subscriber u ON ct.subscriber.email = u.email")
    SubscriptionToken findBySubscriber(Subscriber subscriber);

    boolean existsBySubscriptionToken(String token);
}
