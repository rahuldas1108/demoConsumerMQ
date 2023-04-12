package com.example.demoConsumerMQ.context;

import com.example.demoConsumerMQ.dtos.UserEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.Message;

@Getter
@Setter
@NoArgsConstructor
public class UserContext implements Context{

    private Message message;
    private UserEvent userEvent;
    private String errorMessage;
    private Long retryCount;
    private String eventId;

    private String userId;

    public UserContext(Message message) {
        this.message = message;
    }
}
