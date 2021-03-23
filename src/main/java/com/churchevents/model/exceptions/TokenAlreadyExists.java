package com.churchevents.model.exceptions;

public class TokenAlreadyExists extends RuntimeException{
        public TokenAlreadyExists() {
            super("Token not found");
        }
    }


