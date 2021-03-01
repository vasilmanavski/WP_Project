package com.churchevents.service;

import com.churchevents.model.User;
import com.churchevents.model.enums.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User register(String email, String password, String repeatPassword, Boolean isSubscribed, Role role);

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException;

    Optional<User> findByEmail(String email);

    void save(User user);

    void timerForVerification(String email);
    List<User> listAllUsers();

}

