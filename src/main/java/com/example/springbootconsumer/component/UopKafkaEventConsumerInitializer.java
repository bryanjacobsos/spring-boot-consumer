package com.example.springbootconsumer.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UopKafkaEventConsumerInitializer {

    @Autowired
    UopKafkaEventConsumer uopKafkaEventConsumer;

    @PostConstruct
    void startConsumer() {
        // this is implemented this way to allow testing of the exception handling
        while (true) {
            uopKafkaEventConsumer.pollConsumer();
        }
    }
}
