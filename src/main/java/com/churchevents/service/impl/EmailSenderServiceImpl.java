package com.churchevents.service.impl;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.repository.ConfirmationTokenRepository;
import com.churchevents.repository.PostRepository;
import com.churchevents.repository.SubscribersRepository;
import com.churchevents.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private JavaMailSender javaMailSender;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private final PostRepository postRepository;
    private final SubscribersRepository subscribersRepository;

    @Autowired
    public EmailSenderServiceImpl(JavaMailSender javaMailSender, ConfirmationTokenRepository confirmationTokenRepository, PostRepository postRepository, SubscribersRepository subscribersRepository) {

        this.javaMailSender = javaMailSender;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.postRepository = postRepository;
        this.subscribersRepository = subscribersRepository;
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
                        + "<div>"
                        + "<img src='cid:rightSideImage' style='float:right;width:50px;height:50px;'/>"
                        + "<div>Successful subscription to our newsletter.</div>"
                        + "</div>"
                        + "</div></body>"
                        + "</html>", true
        );
        helper.addInline("rightSideImage",
                new File("src/main/resources/images/emk-logo.jpg"));

        javaMailSender.send(message);

        System.out.println("Email sending complete.");
        }

    catch (Exception e){
        e.printStackTrace();
        }
    }

    @Override
    public void newsletterMail(String fileToAttach, List<Subscriber> subscribers) {

        for (Subscriber subscriber : subscribers) {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {

                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(subscriber.getEmail()));
                    mimeMessage.setFrom(new InternetAddress("vasilmanavski@gmail.com"));
                    mimeMessage.setSubject("We have updated our newsletter");
                    mimeMessage.setText("Lets go");
                    messageBodyPart.setContent(mimeMessage, "text/html");


                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart attachPart = new MimeBodyPart();
                    String attach = fileToAttach;
                    attachPart.attachFile(attach);
                    multipart.addBodyPart(attachPart);

                }
            };

            try {
                javaMailSender.send(preparator);
            } catch (MailException e) {
                e.printStackTrace();
            }

        }
    }
    public String getEmails(List<Subscriber> subscribers){
        List<String> emails = new ArrayList<>();
        for(Subscriber subscriber : subscribers){
            emails.add(subscriber.getEmail());
        }
        return StringUtils.join(emails, ", ");
    }

}




