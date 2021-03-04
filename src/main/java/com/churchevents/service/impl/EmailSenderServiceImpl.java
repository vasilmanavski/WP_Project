package com.churchevents.service.impl;

import com.churchevents.model.User;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.repository.ConfirmationTokenRepository;
import com.churchevents.repository.PostRepository;
import com.churchevents.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private JavaMailSender javaMailSender;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private final PostRepository postRepository;

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender javaMailSender, ConfirmationTokenRepository confirmationTokenRepository, PostRepository postRepository) {

        this.javaMailSender = javaMailSender;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public SimpleMailMessage formRegistrationEmail(User user) {

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        this.confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete your Registration!");
        mailMessage.setFrom("noreply@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());

        return mailMessage;
    }

    @Override
    public void formRegistrationEmailForSubscription(String email) throws MessagingException {

    try {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("noreply@gmail.com");
        helper.setTo(email);
        helper.setSubject("Markuskirche Köln");
        helper.setText(
                "<html>"
                        + "<body>"
                        + "<div>Thank you,"
                        + "<div>"
                        + "<img src='cid:rightSideImage' style='float:right;width:50px;height:50px;'/>"
                        + "<div>Successful subscription to our newsletter.</div>"
                        + "</div>"
                        + "<div>Thanks,</div>"
                        + "</div></body>"
                        + "</html>", true
        );
        helper.addInline("rightSideImage",
                new File("C:/Users/User/Desktop/emk-logo.jpg"));


//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(email);
//        mailMessage.setSubject("Markuskirche Köln");
//        mailMessage.setFrom("noreply@gmail.com");
//        mailMessage.setText("Thank you for subscribing to our newsletter");
        javaMailSender.send(message);
        System.out.println("Email sending complete.");
        }

    catch (Exception e){
        e.printStackTrace();
        }
    }
}




