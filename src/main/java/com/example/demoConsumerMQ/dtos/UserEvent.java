package com.example.demoConsumerMQ.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEvent {

    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;

}
