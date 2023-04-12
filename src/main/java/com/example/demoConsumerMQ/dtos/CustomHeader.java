package com.example.demoConsumerMQ.dtos;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CustomHeader {
    private Long count;
    private String errorMessage;
    private String eventId;
    private String timestamp;
}
