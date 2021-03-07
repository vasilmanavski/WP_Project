package com.churchevents.repository;

import com.churchevents.model.Subscriber;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscribersRepository extends JpaRepository<Subscriber, String> {

    boolean existsByEmail(String email);
    Optional<Subscriber> findByEmail(String email);

}
