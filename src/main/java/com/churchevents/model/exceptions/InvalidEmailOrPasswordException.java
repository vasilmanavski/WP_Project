package com.churchevents.model.exceptions;

public class InvalidEmailOrPasswordException extends RuntimeException{
    public InvalidEmailOrPasswordException() {
        super("Invalid email or password exception");
    }
}
