package com.churchevents.service;

import com.churchevents.model.User;
import com.churchevents.model.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(String email, String password, String repeatPassword, Boolean isSubscribed, Role role);
}
