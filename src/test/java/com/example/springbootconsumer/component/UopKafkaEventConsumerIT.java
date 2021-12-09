package com.example.springbootconsumer.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EmbeddedKafka(brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@SpringBootTest
@ActiveProfiles("test")
public class UopKafkaEventConsumerIT {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.in}")
    private String topic;

    @Autowired
    MockAbstractConsumer abstractConsumer;

    @Test
    public void shouldShutdownConsumer() throws InterruptedException {

        kafkaTemplate.send(topic, "i'll send an sos to the world");

        abstractConsumer.getLatch().await(1, TimeUnit.SECONDS);

        assertTrue(abstractConsumer.getConsumerRecords().size() == 1);
    }
}
