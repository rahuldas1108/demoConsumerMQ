package com.example.demoConsumerMQ.services;

import com.example.demoConsumerMQ.context.Context;
import com.example.demoConsumerMQ.context.UserContext;
import com.example.demoConsumerMQ.utils.MessageUtil;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValidateMessageRetryCountStep implements Step {

    private final Step publishMessageToParkingLotQueueStep;
    private final Step userEventExtractionAndValidationStep;
    @Value("${consumer.message-retry-count}")
    private Long retryThreshold;

    public ValidateMessageRetryCountStep(Step publishMessageToParkingLotQueueStep, Step userEventExtractionAndValidationStep) {
        this.publishMessageToParkingLotQueueStep = publishMessageToParkingLotQueueStep;
        this.userEventExtractionAndValidationStep = userEventExtractionAndValidationStep;
    }

    @Override
    public Step execute(Context context) {
        UserContext userContext=(UserContext) context;
        Message message = userContext.getMessage();
        Long retryCount = MessageUtil.getRetryCount(message);
        userContext.setRetryCount(retryCount + 1);

        if (MessageUtil.hasExceededRetryCount(retryCount, retryThreshold)) {
            return publishMessageToParkingLotQueueStep;
        }
        return userEventExtractionAndValidationStep;

    }
}
