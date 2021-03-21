package com.churchevents.config;

import com.churchevents.model.User;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.core.Authentication;

public class UserSubscriptionInterceptor extends ChannelInterceptorAdapter {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(stompHeaderAccessor.getCommand())) {
            if (stompHeaderAccessor.getUser() == null) {
                throw new IllegalArgumentException("User not authenticated.");
            }
            if (stompHeaderAccessor.getDestination() == null) {
                throw new IllegalArgumentException("StompHeaderAccessor destination not present.");
            }

            Authentication authentication = ((Authentication)stompHeaderAccessor.getUser());
            String userId = ((User)authentication.getPrincipal()).getEmail();
            String userIdFromDestination = stompHeaderAccessor.getDestination().split("/")[2];
            if (!userId.equals(userIdFromDestination)) {
                throw new IllegalArgumentException("No permission for this user.");
            }
        }
        return message;
        //return super.preSend(message, channel);
    }
}
