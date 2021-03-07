package com.churchevents.web;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.*;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.model.tokens.SubscriptionToken;
import com.churchevents.service.EmailSenderService;
import com.churchevents.service.SubscribersService;
import com.churchevents.service.SubscriptionTokenService;
import com.churchevents.service.UserService;
import org.dom4j.rule.Mode;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import java.util.List;

@Controller
@RequestMapping("/subscribers")
public class SubscribersController {
    private final SubscribersService subscribersService;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    private SubscriptionTokenService subscriptionTokenService;

    public SubscribersController(SubscribersService subscribersService, EmailSenderService emailSenderService, UserService userService, SubscriptionTokenService subscriptionTokenService) {
        this.subscribersService = subscribersService;
        this.emailSenderService = emailSenderService;
        this.userService = userService;
        this.subscriptionTokenService = subscriptionTokenService;
    }


    @PostMapping("/save")
    public String save(RedirectAttributes redirectAttributes,
                       @RequestParam String email){

        try {

            this.subscribersService.save(email, false);

            Subscriber subscriber = new Subscriber(email, false);
            SimpleMailMessage mailMessage = this.emailSenderService.formSubscriptionEmail(subscriber);
            this.emailSenderService.sendEmail(mailMessage);
            this.subscribersService.timerForVerification(email);

            redirectAttributes.addFlashAttribute("email", email);

        }
        catch (EmailAlreadyExistsException | InvalidEmailOrPasswordException exception) {
            redirectAttributes.addFlashAttribute("subscriberEmailError", true);
        }

        return "redirect:/posts";
    }

    @GetMapping("/confirm-subscription")
    public String confirmSubscriber(Model model, @RequestParam("token")String subscriptionToken) throws MessagingException {

        SubscriptionToken token = subscriptionTokenService.findBySubscriptionToken(subscriptionToken);

        if(token == null){
            model.addAttribute("message", "The link is invalid or broken");
            return "error";
        }
        else{
            Subscriber subscriber = subscribersService.findByEmail(token.getSubscriber().getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
            subscriber.setIsEnabled(true);
            subscribersService.save(subscriber.getEmail(), subscriber.getIsEnabled());
            emailSenderService.formRegistrationEmailForSubscription(subscriber.getEmail());
            model.addAttribute("bodyContent", "accountVerified");
            return "master-template";
        }
    }

    @GetMapping("/unsubscribe")
    public String getUnsubscribeForm(Model model){

        model.addAttribute("bodyContent", "unsubscribe-form");
        return "master-template";

    }

    @PostMapping("/unsubscribe")
    public String unSubscription(@RequestParam String email, RedirectAttributes redirectAttributes){

        try {
            Subscriber subscriber = this.subscribersService.findByEmail(email)
                    .orElseThrow(EmailNotFoundException::new);

            SubscriptionToken subscriptionToken = this.subscriptionTokenService.findBySubscriber(subscriber);

            if(subscriptionToken != null && this.subscriptionTokenService.checkIfTokenExists(subscriptionToken.getSubscriptionToken())){
                this.subscribersService.deleteTokenAndSubscriber(subscriptionToken, subscriber);
            }
            else{
                this.subscribersService.delete(subscriber);
            }

            redirectAttributes.addFlashAttribute(email);
        }

        catch (TokenNotFound | EmailNotFoundException e){
            redirectAttributes.addFlashAttribute("subscriberEmailError", true);
        }

        return "redirect:/subscribers/unsubscribe";
    }
}
