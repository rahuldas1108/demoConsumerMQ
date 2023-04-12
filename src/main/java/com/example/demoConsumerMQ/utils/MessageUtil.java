package com.example.demoConsumerMQ.utils;

import com.example.demoConsumerMQ.context.UserContext;
import com.example.demoConsumerMQ.dtos.CustomHeader;
import com.example.demoConsumerMQ.dtos.UserEvent;
import com.example.demoConsumerMQ.exception.EntityParsingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class MessageUtil {

    private static final String CUSTOM_HEADER = "custom_header";
    private static final String X_DEATH_COUNT_HEADER_KEY = "count";
    private static final Long DEFAULT_COUNT = 0L;
    private static final Long X_DEATH_HEADER_SIZE = 1L;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static UserEvent parseMessageToUserEntity(UserContext userContext) {
        Message message = userContext.getMessage();
        UserEvent userEvent;
        try {
            userEvent = objectMapper.readValue(message.getBody(), UserEvent.class);
        } catch (IOException e) {
            throw new EntityParsingException("Entity parsing failed");
        }
        return userEvent;
    }
    public static void populateCustomMessageHeaderWithExceptionDetails(Message failedMessage, String errorMessage,
                                                                       Long count, String eventId) {
        List<CustomHeader> errorLogs = (List<CustomHeader>) failedMessage.getMessageProperties()
                .getHeaders().get(CUSTOM_HEADER);
        if (CollectionUtils.isEmpty(errorLogs)) {
            errorLogs = new ArrayList<>();
        }
        CustomHeader customHeader = new CustomHeader();
        customHeader.setCount(count);
        customHeader.setEventId(eventId);
        customHeader.setErrorMessage(errorMessage);
        customHeader.setTimestamp(new Date().toString());
        errorLogs.add(customHeader);
        failedMessage.getMessageProperties().setHeader(CUSTOM_HEADER, errorLogs);
    }
    public static Long getRetryCount(Message incomingMessage) {
        List<Map<String, ?>> xDeathHeader = incomingMessage.getMessageProperties().getXDeathHeader();
        return (!CollectionUtils.isEmpty(xDeathHeader) && xDeathHeader.size() >= X_DEATH_HEADER_SIZE) ?
                (Long) xDeathHeader.get(0).get(X_DEATH_COUNT_HEADER_KEY) : DEFAULT_COUNT;
    }

    public static boolean hasExceededRetryCount(Long retryCount, Long retryThreshold) {
        return retryCount >= retryThreshold;
    }

}
