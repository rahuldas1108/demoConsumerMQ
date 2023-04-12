package com.example.demoConsumerMQ.services;

import com.example.demoConsumerMQ.context.Context;

public interface Step {
    Step execute(Context context);
}
