package com.example.springbootconsumer.component;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Profile("test")
@Component
public class MockAbstractConsumer extends AbstractConsumer {

    public static final Integer NUM_MESSAGES = 1;

    private CountDownLatch latch = new CountDownLatch(NUM_MESSAGES);

    private List<ConsumerRecord<String, String>> consumerRecords = new ArrayList();

    @Override

    public void consume(ConsumerRecord<String, String> consumerRecord) {
        System.out.println("performing test things: " + consumerRecord.value());

        consumerRecords.add(consumerRecord);

        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public List<ConsumerRecord<String, String>> getConsumerRecords() {
        return this.consumerRecords;
    }
}
