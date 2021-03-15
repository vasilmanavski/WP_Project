package com.churchevents.model.events;

import com.churchevents.model.Subscriber;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class SubscriberEmailSentEvent extends ApplicationEvent {

    private LocalDateTime when;
    private String email;

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
