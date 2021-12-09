package com.example.springbootconsumer.component;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
class MockAbstractConsumer extends AbstractConsumer {

    @Override
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        System.out.println("performing test things: " + consumerRecord.value());
//            uopKafkaEventConsumerInitializer.shutdown();
    }
}
