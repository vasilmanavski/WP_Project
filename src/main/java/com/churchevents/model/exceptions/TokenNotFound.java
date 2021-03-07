package com.churchevents.model.exceptions;

public class TokenNotFound extends RuntimeException{
    public TokenNotFound() {
        super("Token not found");
    }
}
