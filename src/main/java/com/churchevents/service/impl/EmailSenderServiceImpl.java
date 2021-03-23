package com.churchevents.service.impl;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import com.churchevents.model.events.EmailSentEvent;
import com.churchevents.model.events.SubscriberEmailSentEvent;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.model.tokens.SubscriptionToken;
import com.churchevents.repository.ConfirmationTokenRepository;
import com.churchevents.repository.SubscriptionTokenRepository;
import com.churchevents.service.EmailSenderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.List;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final SubscriptionTokenRepository subscriptionTokenRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public EmailSenderServiceImpl(JavaMailSender javaMailSender, ConfirmationTokenRepository confirmationTokenRepository,
                                  SubscriptionTokenRepository subscriptionTokenRepository,
                                  ApplicationEventPublisher applicationEventPublisher) {
        this.javaMailSender = javaMailSender;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.subscriptionTokenRepository = subscriptionTokenRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    @Async
    public void formRegistrationEmail(User user) {

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        this.confirmationTokenRepository.save(confirmationToken);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete your Registration!");
            mailMessage.setFrom("EmKCologne@mail.com");
            mailMessage.setText("To confirm your account, please click here : "
                    + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());

            javaMailSender.send(mailMessage);
            this.applicationEventPublisher.publishEvent(new EmailSentEvent(user));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void formSubscriptionEmail(Subscriber subscriber){
        SubscriptionToken subscriptionToken = new SubscriptionToken(subscriber);
        this.subscriptionTokenRepository.save(subscriptionToken);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(subscriber.getEmail());
            mailMessage.setSubject("Complete your Subscription!");
            mailMessage.setFrom("EmKCologne@mail.com");
            mailMessage.setText("To confirm your subscription, please click here : "
                    + "http://localhost:8080/subscribers/confirm-subscription?token=" + subscriptionToken.getSubscriptionToken());

            javaMailSender.send(mailMessage);
            this.applicationEventPublisher.publishEvent(new SubscriberEmailSentEvent(subscriber));

        }

        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    @Async
    public void formRegistrationEmailForSubscription(String email) throws MessagingException {

    try {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("EmKCologne@mail.com");
        helper.setTo(email);
        helper.setSubject("Markuskirche Köln");
        helper.setText(
                "<html>"
                        + "<body>"
                        + "<div>"
                        + "<img src='cid:rightSideImage' style='float:right;width:50px;height:50px;'/>"
                        + "<div>Successful subscription to our newsletter.</div>"
                        + "</div>"
                        + "You can unsubscribe at any time by clicking the following link: https://markuskirche.herokuapp.com/subscribers/unsubscribe"
                        + "</div></body>"
                        + "</html>", true
        );
        helper.addInline("rightSideImage",
                new File("src/main/resources/images/emk-logo.jpg"));

        javaMailSender.send(message);

        }

    catch (Exception e){
        e.printStackTrace();
        }
    }

    @Override
    @Async
    public void newsletterMail( List<Subscriber> subscribers) throws MessagingException {

        try{
        for (Subscriber subscriber : subscribers) {

                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(new InternetAddress(subscriber.getEmail()));
                helper.setFrom(new InternetAddress("EmKCologne@mail.com"));
                helper.setSubject("Markuskirche Köln weekly newsletter");
                helper.setText(
                         "We have new article in our newsletter. "
                        + "You can check it out by clicking on the following link: "

                        + "https://markuskirche.herokuapp.com/getPDF"

                );
                javaMailSender.send(message);
            }
        }
            catch (MailException e) {
                e.printStackTrace();
            }

        }
    }





