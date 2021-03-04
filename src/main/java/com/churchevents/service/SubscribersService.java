package com.churchevents.service;

import com.churchevents.model.Subscriber;

import java.util.List;

public interface SubscribersService {
    List<Subscriber> findAll();
    void save(String email);
    boolean findByEmail(String email);
}
