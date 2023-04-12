package com.example.demoConsumerMQ.exception;


public class EntityParsingException extends RuntimeException {
    public EntityParsingException(String errorMessage) {
        super(errorMessage);
    }
}
