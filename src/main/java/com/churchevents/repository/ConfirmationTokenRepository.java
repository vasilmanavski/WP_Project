package com.churchevents.repository;

import com.churchevents.model.User;
import com.churchevents.model.tokens.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
    ConfirmationToken findByUser(User user);
}
