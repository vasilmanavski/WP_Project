package com.churchevents.model.exceptions;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException() {
        super("Email not found");
    }
}
