package com.example.springbootconsumer.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class UopKafkaEventConsumerInitializer {

    @Autowired
    AbstractConsumer abstractConsumer;

    private final AtomicBoolean closed = new AtomicBoolean(false);

    @PostConstruct
    void startConsumer() {

        Runnable runnable = () -> {

            while (!closed.get()) {
                abstractConsumer.pollConsumer();
            }

        };

        new Thread(runnable).start();
    }

    @PreDestroy
    void shutdownConsumer() {
        shutdown();
    }

    public void shutdown() {
        closed.set(true);
        abstractConsumer.kafkaConsumer.wakeup();
    }


}
