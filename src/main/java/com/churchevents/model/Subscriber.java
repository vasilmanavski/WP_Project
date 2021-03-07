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

    private Boolean isEnabled = false;


    public Subscriber(String email, Boolean isEnabled) {
        this.email = email;
        this.isEnabled = isEnabled;
    }

    public Subscriber() {

    }

//
//    public Boolean isEnabled() {
//        return isEnabled;
//    }
//
//    public void setIsEnabled(Boolean isEnabled){
//        this.isEnabled = isEnabled;
//    }
}
