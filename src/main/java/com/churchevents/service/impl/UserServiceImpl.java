package com.churchevents.service.impl;

import com.churchevents.model.enums.Role;
import com.churchevents.model.exceptions.EmailAlreadyExistsException;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.model.exceptions.PasswordsDoNotMatchException;
import com.churchevents.model.tokens.ConfirmationToken;
import com.churchevents.repository.ConfirmationTokenRepository;
import com.churchevents.repository.UserRepository;
import com.churchevents.service.UserService;
import com.churchevents.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ConfirmationTokenRepository confirmationTokenRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public void save(User user) {
        this.userRepository.save(user);
    }

    @Override
    public void timerForVerification(User user) {
        CompletableFuture.delayedExecutor(600, TimeUnit.SECONDS).execute(() -> {
            if(!user.isEnabled()){
                ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByUser(user);
                this.confirmationTokenRepository.delete(confirmationToken);
                this.userRepository.delete(user);
            }
        });
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


}
