package com.churchevents.service;

import com.churchevents.model.User;
import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;

public interface EmailSenderService {

    void sendEmail(SimpleMailMessage email);
    SimpleMailMessage formRegistrationEmail(User user);
    void formRegistrationEmailForSubscription(String email) throws MessagingException;
}
