package com.churchevents.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "subscribers")
public class Subscriber {
    @Id
    private String email;

    public Subscriber(String email) {
        this.email = email;
    }

    public Subscriber() {

    }
}
