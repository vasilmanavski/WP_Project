package com.churchevents.web;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import com.churchevents.model.exceptions.EmailAlreadyExistsException;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.service.EmailSenderService;
import com.churchevents.service.SubscribersService;
import com.churchevents.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    public SubscribersController(SubscribersService subscribersService, EmailSenderService emailSenderService, UserService userService) {
        this.subscribersService = subscribersService;
        this.emailSenderService = emailSenderService;
        this.userService = userService;
    }

    @PostMapping("/save")
    public String save(RedirectAttributes redirectAttributes,
                       @RequestParam String email){

        try {

            this.subscribersService.save(email);
            this.emailSenderService.formRegistrationEmailForSubscription(email);
            redirectAttributes.addFlashAttribute("email", email);

        }
        catch (EmailAlreadyExistsException | InvalidEmailOrPasswordException | MessagingException exception) {
            redirectAttributes.addFlashAttribute("subscriberEmailError", true);
        }

        return "redirect:/posts";
    }


}
