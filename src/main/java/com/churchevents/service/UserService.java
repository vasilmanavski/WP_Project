package com.churchevents.service;

import com.churchevents.model.Post;
import com.churchevents.model.User;

import java.util.List;

public interface UserService {
    User create(String username, String password, boolean isSubscribed);
    User findByEmail(String email);
    List<User> listAllUsers();
}
