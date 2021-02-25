package com.churchevents.web;

import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.InvalidEmailOrRoleException;
import com.churchevents.model.exceptions.PasswordsDoNotMatchException;
import com.churchevents.service.RoleService;
import com.churchevents.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addUserToRole(Model model){

        List<Role> roles = this.roleService.findAllRoles();
        model.addAttribute("roles", roles);

        return "addRole";
    }

    @PostMapping("/Roles")
    public String addUserToRole(@RequestParam String email,
                                @RequestParam Role role,
                                Model model){

        try {
            this.roleService.updateRole(email, role);
            return "redirect:/home";
        }
        catch(InvalidEmailOrRoleException | PasswordsDoNotMatchException exception) {
            return "error";
        }
    }
}
