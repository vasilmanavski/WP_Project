package com.churchevents.repository;

import com.churchevents.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscribersRepository extends JpaRepository<Subscriber, String> {

    boolean existsByEmail(String email);
}
