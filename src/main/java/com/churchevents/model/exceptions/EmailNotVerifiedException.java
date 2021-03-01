package com.churchevents.model.exceptions;

public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException() {
        super("Verify your email");
    }
}
