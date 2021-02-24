package com.churchevents.service;

import com.churchevents.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

public interface EmailSenderService {

    void sendEmail(SimpleMailMessage email);
    SimpleMailMessage formEmail(User user);
}
