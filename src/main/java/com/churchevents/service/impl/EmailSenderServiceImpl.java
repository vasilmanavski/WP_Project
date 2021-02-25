package com.churchevents.service.impl;

import com.churchevents.model.User;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.repository.ConfirmationTokenRepository;
import com.churchevents.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private JavaMailSender javaMailSender;
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender javaMailSender, ConfirmationTokenRepository confirmationTokenRepository) {

        this.javaMailSender = javaMailSender;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public SimpleMailMessage formEmail(User user) {

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        this.confirmationTokenRepository.save(confirmationToken);

        // use MimeMessageHelper to send mails with attachments

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete your Registration!");
        mailMessage.setFrom("noreply@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
        + "http://localhost:8080/confirm-account?token="+confirmationToken.getConfirmationToken());

        return mailMessage;
    }


}
