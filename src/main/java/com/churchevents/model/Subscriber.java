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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Subscriber(String email, Boolean isEnabled) {
        this.email = email;
        this.isEnabled = isEnabled;
    }

    public Subscriber() {

    }

}
