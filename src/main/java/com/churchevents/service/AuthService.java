package com.churchevents.service;

import com.churchevents.model.User;

public interface AuthService {
    User login(String email, String password);
}
