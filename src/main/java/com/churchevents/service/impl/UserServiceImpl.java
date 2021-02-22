package com.churchevents.service.impl;

import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.EmailAlreadyExistsException;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.PasswordsDoNotMatchException;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.UserService;
import com.churchevents.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String email, String password, String repeatPassword, Boolean isSubscribed, Role role) {

        if (email==null || email.isEmpty()  || password==null || password.isEmpty())
              throw new InvalidEmailOrPasswordException();
        if (!password.equals(repeatPassword))
              throw new PasswordsDoNotMatchException();
        if(this.userRepository.findByEmail(email).isPresent())
              throw new EmailAlreadyExistsException(email);
        User user = new User(email,passwordEncoder.encode(password),isSubscribed,role);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException(s));
    }
}
