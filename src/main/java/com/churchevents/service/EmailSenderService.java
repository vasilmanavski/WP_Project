package com.churchevents.service;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;

import javax.mail.MessagingException;
import java.util.List;

public interface EmailSenderService {

    void formRegistrationEmail(User user);
    void formSubscriptionEmail(Subscriber subscriber);

    void formRegistrationEmailForSubscription(String email) throws MessagingException;
    void newsletterMail( List<Subscriber> subscribers) throws MessagingException;
}
