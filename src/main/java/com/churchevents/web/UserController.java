package com.churchevents.web;

import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.EmailNotFoundException;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.InvalidEmailOrRoleException;
import com.churchevents.model.exceptions.PasswordsDoNotMatchException;
import com.churchevents.service.RoleService;
import com.churchevents.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/User")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/Roles")
//    @PreAuthorize("hasRole('ROLE_ADMIN')") to do
    public String addUserToRole(@RequestParam(required = false) String error, Model model){

        if(error != null && !error.isEmpty()){
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        List<Role> roles = this.roleService.findAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("bodyContent", "addRole");

        return "master-template";
    }

    @PostMapping("/Roles")
    public String addUserToRole(@RequestParam String email,
                                @RequestParam Role role,
                                RedirectAttributes redirectAttributes){

        try {
            this.roleService.updateRole(email, role);
            redirectAttributes.addFlashAttribute("message", "Role successfully updated.");
            return "redirect:/User/Roles";
        }
        catch(InvalidEmailOrRoleException | EmailNotFoundException exception) {
            return "redirect:/User/Roles?error=" + exception.getMessage();
        }
    }
}
