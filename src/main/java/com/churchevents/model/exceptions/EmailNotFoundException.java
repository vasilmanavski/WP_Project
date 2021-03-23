package com.churchevents.model.exceptions;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException() {
        super("User with that email doesn't exists");
    }
}
