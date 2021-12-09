package com.example.springbootconsumer.component;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
public class UopKafkaEventConsumer extends AbstractConsumer {

    @Override
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        System.out.println("sending to some destination: " + consumerRecord);
    }
}
