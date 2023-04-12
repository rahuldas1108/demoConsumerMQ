package com.example.demoConsumerMQ.services;

import com.example.demoConsumerMQ.context.UserContext;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListenerService {

    private final Step validateMessageRetryCountStep;

    public MessageListenerService(Step validateMessageRetryCountStep) {
        this.validateMessageRetryCountStep = validateMessageRetryCountStep;
    }

    @RabbitListener(queues = "${consumer.queue.name}")
    public void consume(Message message){
        UserContext userContext= new UserContext(message);
        Step nextStep = validateMessageRetryCountStep;
        while(nextStep!=null){
            nextStep=nextStep.execute(userContext);
        }

    }
}
