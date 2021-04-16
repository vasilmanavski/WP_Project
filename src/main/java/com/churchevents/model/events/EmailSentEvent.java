package com.churchevents.model.events;

import com.churchevents.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class EmailSentEvent extends ApplicationEvent {

    private LocalDateTime when;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailSentEvent(User source) {
        super(source);
        this.when = LocalDateTime.now();
        this.email = source.getEmail();
    }

}
