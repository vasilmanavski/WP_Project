package com.churchevents.service;

import com.churchevents.model.Subscriber;
import com.churchevents.model.tokens.SubscriptionToken;

import javax.swing.text.StyledEditorKit;
import java.util.List;
import java.util.Optional;

public interface SubscribersService {
    List<Subscriber> findAll();
    void save(String email, Boolean isEnabled);
    Optional<Subscriber> findByEmail(String email);
    void timerForVerification(String email);
    void delete(Subscriber subscriber);
    void deleteTokenAndSubscriber(SubscriptionToken token, Subscriber subscriber);
}
