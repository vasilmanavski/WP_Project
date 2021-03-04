package com.churchevents.service.impl;

import com.churchevents.model.Subscriber;
import com.churchevents.model.exceptions.EmailAlreadyExistsException;
import com.churchevents.model.exceptions.InvalidEmailOrPasswordException;
import com.churchevents.repository.SubscribersRepository;
import com.churchevents.service.SubscribersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscribersServiceImpl implements SubscribersService {

    private final SubscribersRepository subscribersRepository;

    public SubscribersServiceImpl(SubscribersRepository subscribersRepository) {
        this.subscribersRepository = subscribersRepository;
    }


    @Override
    public List<Subscriber> findAll() {
        return this.subscribersRepository.findAll();
    }

    @Override
    public void save(String email) {
        if(email.isEmpty() || email == null) throw new InvalidEmailOrPasswordException();
        if(findByEmail(email)) throw new EmailAlreadyExistsException(email);

        Subscriber subscriber = new Subscriber(email);
        this.subscribersRepository.save(subscriber);

    }

    @Override
    public boolean findByEmail(String email) {
        if(this.subscribersRepository.existsByEmail(email))
            return true;
        else
            return false;
    }
}
