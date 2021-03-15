package com.churchevents.listeners;

import com.churchevents.model.events.EmailSentEvent;
import com.churchevents.model.events.SubscriberEmailSentEvent;
import com.churchevents.service.SubscribersService;
import com.churchevents.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailEventHandler {

    private final UserService userService;
    private final SubscribersService subscribersService;

    public EmailEventHandler(UserService userService, SubscribersService subscribersService) {
        this.userService = userService;
        this.subscribersService = subscribersService;
    }

    @EventListener
    public void onUserCreated(EmailSentEvent event){
        this.userService.timerForVerification(event);
    }

    @EventListener
    public void onSubscriberCreated(SubscriberEmailSentEvent event){
        this.subscribersService.timerForVerification(event);
    }
}
