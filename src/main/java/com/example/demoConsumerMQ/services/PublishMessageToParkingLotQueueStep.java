package com.example.demoConsumerMQ.services;

import com.example.demoConsumerMQ.context.Context;
import com.example.demoConsumerMQ.context.UserContext;
import com.example.demoConsumerMQ.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class PublishMessageToParkingLotQueueStep implements Step {

    private static final String ERROR = "error";
    private static final String PUBLISHING_MESSAGE_TO_PARKING_LOT=
            "Error occurred while processing event. Publishing Message to Parking Lot Queue";

    private final RabbitTemplate rabbitTemplate;
    @Value("${consumer.dl-exchange.name}")
    private String dlExchange;
    @Value("${consumer.parking-lot-queue.routing-key}")
    private String parkingLotRoutingKey;
    @Value("${consumer.message-retry-count}")
    private Long retryThreshold;

    public PublishMessageToParkingLotQueueStep(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Step execute(Context context) {
        UserContext userContext = (UserContext) context;
        Message failedMessage = userContext.getMessage();
        String errorMessage = userContext.getErrorMessage();
        Long retryCount = userContext.getRetryCount();
        log.info(PUBLISHING_MESSAGE_TO_PARKING_LOT,
                userContext,
                ERROR, errorMessage);
        if (retryCount <= retryThreshold) {
            MessageUtil.populateCustomMessageHeaderWithExceptionDetails(
                    failedMessage, errorMessage, retryCount, userContext.getEventId());
        }
        this.rabbitTemplate.send(dlExchange, parkingLotRoutingKey, failedMessage);
        return null;
    }
}
