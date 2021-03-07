package com.churchevents.model.exceptions;

public class SubscriberNotFound extends RuntimeException{
    public SubscriberNotFound() {
        super("Subscriber not found");
    }
}
