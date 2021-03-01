package com.churchevents.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "email_users")

public class User {
    @Id
    private String email;

    private String password;

    private boolean isSubscribed;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Post> posts;

    public User(String email, String password, boolean isSubscribed) {
        this.email = email;
        this.password = password;
        this.isSubscribed = isSubscribed;
    }

    public User() {
    }
}
