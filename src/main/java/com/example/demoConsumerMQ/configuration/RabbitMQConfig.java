package com.example.demoConsumerMQ.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RabbitMQConfig {

    @Value("${consumer.exchange.name}")
    private String exchange;

    @Value("${consumer.dl-exchange.name}")
    private String dlExchange;

    @Value("${consumer.queue.name}")
    private String queue;

    @Value("${consumer.queue.routing-keys}")
    private List<String> routingKeys;

    @Value("${consumer.dead-letter-queue.name}")
    private String deadLetterQueue;

    @Value("${consumer.dead-letter-queue.routing-key}")
    private String deadLetterRoutingKey;

    @Value("${consumer.retry-queue.name}")
    private String retryQueue;

    @Value("${consumer.retry-queue.routing-key}")
    private String retryRoutingKey;

    @Value("${consumer.parking-lot-queue.name}")
    private String parkingLotQueue;

    @Value("${consumer.parking-lot-queue.routing-key}")
    private String parkingLotRoutingKey;

    @Value("${consumer.message-ttl}")
    private int timeToLive;


    @Bean
    TopicExchange deadLetterExchange() {
        return new TopicExchange(dlExchange);
    }


    @Bean
    Queue primaryQueue() {
        return QueueBuilder.durable(queue).deadLetterExchange(dlExchange)
                .deadLetterRoutingKey(parkingLotRoutingKey).build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueue).deadLetterExchange(dlExchange)
                .deadLetterRoutingKey(retryRoutingKey).ttl(timeToLive).build();
    }

    @Bean
    Queue parkingLotQueue() {
        return new Queue(parkingLotQueue);
    }

    @Bean
    Declarables primaryQueueBinding(Queue primaryQueue) {
        return new Declarables(routingKeys.stream().map(key ->
                BindingBuilder.bind(primaryQueue).
                        to(new TopicExchange(exchange)).with(key))
                .collect(Collectors.toList()));
    }

    @Bean
    Binding dlQueueBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(deadLetterRoutingKey);
    }

    @Bean
    Binding parkingLotQueueBinding(Queue parkingLotQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(parkingLotQueue).to(deadLetterExchange).with(parkingLotRoutingKey);
    }

    @Bean
    Binding retryQueueBinding(TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(new Queue(retryQueue)).to(deadLetterExchange).with(retryRoutingKey);
    }

    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public RabbitTemplate getRabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
