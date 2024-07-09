package com.example.exception;

public class FraudDetectionException extends RuntimeException {
    public FraudDetectionException(String message) {
        super(message);
    }
}

