package com.churchevents.service;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;
import java.io.File;
import java.util.List;

public interface EmailSenderService {

    void sendEmail(SimpleMailMessage email);
    SimpleMailMessage formRegistrationEmail(User user);
    SimpleMailMessage formSubscriptionEmail(Subscriber subscriber);

    void formRegistrationEmailForSubscription(String email) throws MessagingException;
    void newsletterMail(String fileToAttach, List<Subscriber> subscribers);
}
