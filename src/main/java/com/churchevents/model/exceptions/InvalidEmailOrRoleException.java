package com.churchevents.model.exceptions;

public class InvalidEmailOrRoleException extends RuntimeException{

    public InvalidEmailOrRoleException() {
        super("Invalid email or password exception");
    }
}
