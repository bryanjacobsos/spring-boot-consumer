package com.example.springbootconsumer.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@EmbeddedKafka(brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@SpringBootTest
@ActiveProfiles("test")
public class UopKafkaEventConsumerIT {

    @Autowired
    private UopKafkaEventConsumerInitializer uopKafkaEventConsumerInitializer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.in}")
    private String topic;

    @Test
    public void shouldShutdownConsumer() throws InterruptedException {

        kafkaTemplate.send(topic, "i'll send an sos to the world");

        // todo: find a way to remove the thread.sleep to get the test to pass
        Thread.sleep(500);




    }
}
