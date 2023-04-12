package com.example.demoConsumerMQ.exception;

public class InvalidOrderEventException extends  RuntimeException {
    public InvalidOrderEventException(String message) {
        super(message);
    }
}
