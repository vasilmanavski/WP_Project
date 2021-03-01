package com.churchevents.model.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email) {
        super(String.format("User with email: %s already exists", email));
    }
}
