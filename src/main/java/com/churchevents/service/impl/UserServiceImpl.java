package com.churchevents.service.impl;

import com.churchevents.model.User;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User create(String username, String password, boolean isSubscribed) {
        String encrypedPassword = this.passwordEncoder.encode(password);
        User user = new User(username,encrypedPassword,isSubscribed);
        return this.userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
         return this.userRepository.findByEmail(email);
    }

    @Override
    public List<User> listAllUsers() {
        return this.userRepository.findAll();
    }

}
