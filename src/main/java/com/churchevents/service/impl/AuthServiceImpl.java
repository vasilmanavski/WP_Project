package com.churchevents.service.impl;

import com.churchevents.model.User;
import com.churchevents.model.exceptions.InvalidUserCredentialsException;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User login(String email, String password) {
        if (email == null || email.isEmpty() ||
                password==null || password.isEmpty());

        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(InvalidUserCredentialsException::new);
    }
}
