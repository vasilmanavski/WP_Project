package com.churchevents.web;

import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.PasswordsDoNotMatchException;
import com.churchevents.service.AuthService;
import com.churchevents.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegisterController {
    private final AuthService authService;
    private final UserService userService;

    public RegisterController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping
    public String getRegisterPage(@RequestParam(required = false) String error, Model model){
        if(error != null && !error.isEmpty()){
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        return "register";
    }

    @PostMapping
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatedPassword,
                           @RequestParam(required = false) Boolean isSubscribed,
                           @RequestParam Role role){
        try {
            if (isSubscribed == null) {
                this.userService.register(email, password, repeatedPassword, false, role);
            }
            else {
                this.userService.register(email, password, repeatedPassword, isSubscribed, role);
            }
            return "redirect:/login";
        } catch(InvalidEmailOrPasswordException | PasswordsDoNotMatchException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }
}
