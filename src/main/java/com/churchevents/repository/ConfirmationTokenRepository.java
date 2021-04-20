package com.churchevents.repository;

import com.churchevents.model.User;
import com.churchevents.model.tokens.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
    ConfirmationToken findByUser(User user);


    @Query(value = "SELECT ct FROM ConfirmationToken ct JOIN User u ON ct.user.email = u.email")
    ConfirmationToken findFirstByUser(User user);

}
