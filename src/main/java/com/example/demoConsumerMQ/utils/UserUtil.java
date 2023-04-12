package com.example.demoConsumerMQ.utils;

import com.example.demoConsumerMQ.dtos.UserEvent;
import com.example.demoConsumerMQ.exception.InvalidOrderEventException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import java.util.Objects;

@Component
public class UserUtil {

    public static void validateUserDetails(UserEvent userEvent) {
        if (ObjectUtils.isEmpty(userEvent) || Objects.isNull(userEvent.getId()) ||
                    Objects.isNull(userEvent.getPhone())) {
            throw new InvalidOrderEventException("Invalid user data for missing fields");
        }
    }

}
