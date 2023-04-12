package com.example.demoConsumerMQ.services;

import com.example.demoConsumerMQ.context.Context;
import com.example.demoConsumerMQ.context.UserContext;
import com.example.demoConsumerMQ.dtos.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserStatusValidationStep implements Step{

    private final Step publishMessageToDeadLetterQueueStep;

    public UserStatusValidationStep(Step publishMessageToDeadLetterQueueStep) {
        this.publishMessageToDeadLetterQueueStep = publishMessageToDeadLetterQueueStep;
    }

    @Override
    public Step execute(Context context) {
        UserContext userContext = (UserContext) context;
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl
                = "https://jsonplaceholder.typicode.com/users/" + userContext.getUserId();
        UserEvent userEvent;
        try{
            userEvent=restTemplate.getForObject(resourceUrl, UserEvent.class);
        }catch (Exception e){
            log.info("exception occured while fetch status of user",e.getMessage());
            userContext.setErrorMessage(e.getMessage());
            return publishMessageToDeadLetterQueueStep;
        }
        log.info("The userId : {} is Active",userContext.getUserId());
        return null;
    }
}
