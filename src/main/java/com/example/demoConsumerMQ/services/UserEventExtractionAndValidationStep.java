package com.example.demoConsumerMQ.services;

import com.example.demoConsumerMQ.context.Context;
import com.example.demoConsumerMQ.context.UserContext;
import com.example.demoConsumerMQ.dtos.UserEvent;
import com.example.demoConsumerMQ.exception.EntityParsingException;
import com.example.demoConsumerMQ.exception.InvalidOrderEventException;
import com.example.demoConsumerMQ.utils.MessageUtil;
import com.example.demoConsumerMQ.utils.UserUtil;
import org.springframework.stereotype.Component;

@Component
public class UserEventExtractionAndValidationStep implements Step{

    private final Step publishMessageToParkingLotQueueStep;
    private final Step userStatusValidationStep;

    public UserEventExtractionAndValidationStep(Step publishMessageToParkingLotQueueStep, Step userStatusValidationStep) {
        this.publishMessageToParkingLotQueueStep = publishMessageToParkingLotQueueStep;
        this.userStatusValidationStep = userStatusValidationStep;
    }

    @Override
    public Step execute(Context context) {
        UserContext userContext= (UserContext) context;
        UserEvent userEvent;
        try{
            userEvent = MessageUtil.parseMessageToUserEntity(userContext);
            UserUtil.validateUserDetails(userEvent);
            userContext.setUserId(userEvent.getId());
            userContext.setEventId(userEvent.getId());
        }
        catch (EntityParsingException | InvalidOrderEventException e) {

            userContext.setErrorMessage(e.getMessage());
            return publishMessageToParkingLotQueueStep;
        }
        return userStatusValidationStep;
    }
}
