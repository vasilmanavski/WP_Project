package com.churchevents.service.impl;


import com.churchevents.model.enums.Role;
import com.churchevents.model.events.EmailSentEvent;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;

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
    public void timerForVerification(EmailSentEvent event) {
        CompletableFuture.delayedExecutor(60, TimeUnit.SECONDS).execute(() -> {

            User user = this.userRepository.findByEmail(event.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException(event.getEmail()));

            ConfirmationToken confirmationToken = this.confirmationTokenRepository.findByUser(user);
            this.confirmationTokenRepository.delete(confirmationToken);

            if (!user.isEnabled()) {
                this.userRepository.delete(user);
            }
        });

    }

    @Override
    public User register(String email, String password, String repeatPassword) {

        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            throw new InvalidEmailOrPasswordException();
        if (!password.equals(repeatPassword))
            throw new PasswordsDoNotMatchException();
        if (this.userRepository.findByEmail(email).isPresent())
            throw new EmailAlreadyExistsException(email);
        User user = new User(email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public List<User> listAllUsers() {
        return this.userRepository.findAll();
    }

}