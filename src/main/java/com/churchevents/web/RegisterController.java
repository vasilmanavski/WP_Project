package com.churchevents.web;

import com.churchevents.model.User;
import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.PasswordsDoNotMatchException;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.service.AuthService;
import com.churchevents.service.ConfirmationTokenService;
import com.churchevents.service.EmailSenderService;
import com.churchevents.service.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {
    private final AuthService authService;
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailSenderService;

    public RegisterController(AuthService authService, UserService userService, ConfirmationTokenService confirmationTokenService, EmailSenderService emailSenderService) {
        this.authService = authService;
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping(value = "/register")
    public String getRegisterPage(@RequestParam(required = false) String error, Model model){
        if(error != null && !error.isEmpty()){
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        return "register";
    }

    @PostMapping(value = "/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatedPassword,
                           @RequestParam(required = false) Boolean isSubscribed,
                           @RequestParam Role role,
                           Model model){
        try {
            if (isSubscribed == null) {
                isSubscribed = false;
            }
            this.userService.register(email, password, repeatedPassword, isSubscribed, role);

            User user = new User(email, password, isSubscribed, role);

            SimpleMailMessage mailMessage = this.emailSenderService.formRegistrationEmail(user);
            this.emailSenderService.sendEmail(mailMessage);
            this.userService.timerForVerification(email);

            model.addAttribute("email", user.getEmail());
            return "successfulRegistration";

        } catch(InvalidEmailOrPasswordException | PasswordsDoNotMatchException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }

    @GetMapping("/confirm-account")
    public String confirmUser(Model model, @RequestParam("token")String confirmationToken){

        ConfirmationToken token = confirmationTokenService.findByConfirmationToken(confirmationToken);

        if(token == null){
            model.addAttribute("message", "The link is invalid or broken");
            return "error";
        }
        else{
            User user = userService.findByEmail(token.getUser().getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
            user.setEnabled(true);
            userService.save(user);
            return "accountVerified";
        }
    }
}
