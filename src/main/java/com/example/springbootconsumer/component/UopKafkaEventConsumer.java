package com.example.springbootconsumer.component;

import com.example.springbootconsumer.exception.ExceptionMessage;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;

@Component
public class UopKafkaEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(UopKafkaEventConsumer.class);

    public static final String NULL_RECORD_MSG = "The ConsumerRecord was null because the poll method threw an exception. No further information available";

    @Value("${input.topic}") // see application.properties
    String inputTopic;

    @Autowired
    KafkaConsumer kafkaConsumer;

    public void pollConsumer() {

        ConsumerRecords<String, String> records = null;
        try {

            records = kafkaConsumer.poll(Duration.ofMillis(100));

            for (var consumerRecord : records) {
                // inject a spring @Service bean to perform the work
                System.out.println("sending to some destination: " + consumerRecord);
            }

            // while this is slower it's less complex and probably fast enough since we will scale using
            // a group of consumers all writing to the destination
            kafkaConsumer.commitSync();

        } catch (Throwable t) {
            if (records == null) {
                var exceptionMessage = ExceptionMessage.Builder.newBuilder()
                        .withUnexpectedException()
                        .withAdditionalInfo(NULL_RECORD_MSG)
                        .build();

                // use the toJson() method because we need to know it happened
                // don't want the exception pipeline to pick it up because can't process it
                LOG.error(exceptionMessage.toJson());
            }
            else {
                records.forEach(record -> {
                    var originalRecord = record.value();
                    var exceptionMessage = ExceptionMessage.Builder.newBuilder()
                            .withUnexpectedException()
                            .withOriginalRecord(originalRecord)
                            .withOffset(record.offset())
                            // fill in all possible details...
                            .build();

                    LOG.error(exceptionMessage.toLogMessage());
                });
            }
        }
    }

    @PostConstruct
    void subscribe() {
        kafkaConsumer.subscribe(Arrays.asList(inputTopic));
    }
}
