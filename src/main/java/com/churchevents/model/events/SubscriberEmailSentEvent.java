package com.churchevents.model.events;

import com.churchevents.model.Subscriber;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class SubscriberEmailSentEvent extends ApplicationEvent {

    private LocalDateTime when;
    private String email;

    public LocalDateTime getWhen() {
        return when;
    }

    public void setWhen(LocalDateTime when) {
        this.when = when;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SubscriberEmailSentEvent(Subscriber source) {
        super(source);
        this.when = LocalDateTime.now();
        this.email = source.getEmail();
    }

    public SubscriberEmailSentEvent(Subscriber source, LocalDateTime when, String email) {
        super(source);
        this.when = when;
    }
}
