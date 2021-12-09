package com.example.springbootconsumer.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@EmbeddedKafka(brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@SpringBootTest
public class UopKafkaEventConsumerIT {

    @Autowired
    UopKafkaEventConsumerInitializer uopKafkaEventConsumerInitializer;

    @Test
    public void shouldShutdownConsumer() {

        uopKafkaEventConsumerInitializer.shutdown();
    }
}
