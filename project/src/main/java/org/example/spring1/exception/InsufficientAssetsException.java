package org.example.spring1.exception;

public class InsufficientAssetsException extends RuntimeException{
    public InsufficientAssetsException(String message) {
        super(message);
    }
}
