package com.example.springbootconsumer.component;

import com.example.springbootconsumer.exception.ExceptionMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.FencedInstanceIdException;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.example.springbootconsumer.exception.ExceptionMessage.EXCEPTION_MESSAGE_DELIMITER;
import static com.example.springbootconsumer.exception.ExceptionMessage.ExceptionType.UNEXPECTED_EXCEPTION;
import static com.example.springbootconsumer.exception.ExceptionMessage.toExceptionMessageRemoveDelimiter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


public class UopKafkaEventConsumerTest {

    private static final String LOG_FILE = "logs/app.log";

    private File file = new File(LOG_FILE);

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void overwriteLogWithEmptyStringSoEasyToFindLoggedMessages() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
    }

    @Test
    public void shouldGetNullConsumerRecordMessage() throws IOException {

        var consumer = new UopKafkaEventConsumer();

        // create mock
        var mockKafkaConsumer = mock(KafkaConsumer.class);

        // mock out poll method to throw a possible exception from that method
        when(mockKafkaConsumer.poll(any(Duration.class))).thenThrow(new FencedInstanceIdException("fenced"));

        // assign mock
        consumer.kafkaConsumer = mockKafkaConsumer;

        // invoked the method so that exception handling will get invoked
        consumer.pollConsumer();

        // Logback is configured to write to a file to the file system....read that logged message in
        var fileContents = Files.readString(Path.of(file.toURI()));

        // convert the json message to a map
        var jsonMap = mapper.readValue(fileContents, Map.class);

        // get the actual message contents for the ExceptionMessage
        var exceptionMessageAsJson = (String)jsonMap.get("message");

        // Convert the ExceptionMessage into its class/object representation
        var exceptionMessage = mapper.readValue(exceptionMessageAsJson, ExceptionMessage.class);

        // should have a valid message to assert against
        assertEquals(UopKafkaEventConsumer.NULL_RECORD_MSG, exceptionMessage.getAdditionalInfo());
    }

    @Test
    public void commitSyncShouldThrowAnException() throws IOException {
        var uopKafkaEventConsumer = new UopKafkaEventConsumer();

        // create mock
        var mockKafkaConsumer = mock(KafkaConsumer.class);

        // mock out poll method to throw a possible exception from that method
        var records =
                List.of(new ConsumerRecord<>("topic", 1, 1, "key", "value"));

        var map =
                Map.of(new TopicPartition("topic", 1), records);

        var consumerRecords = new ConsumerRecords<>(map);

        when(mockKafkaConsumer.poll(any(Duration.class))).thenReturn(consumerRecords);
        doThrow(new TimeoutException("too long")).when(mockKafkaConsumer).commitSync();

        uopKafkaEventConsumer.kafkaConsumer = mockKafkaConsumer;

        uopKafkaEventConsumer.pollConsumer();

        // Logback is configured to write to a file to the file system....read that logged message in
        var fileContents = Files.readString(Path.of(file.toURI()));

        assertTrue(fileContents.contains(EXCEPTION_MESSAGE_DELIMITER));

        var jsonMap = mapper.readValue(fileContents, Map.class);

        var exceptionMessageWithDelimiter = (String) jsonMap.get("message");

        var exceptionMessage = toExceptionMessageRemoveDelimiter(exceptionMessageWithDelimiter);

        assertEquals(UNEXPECTED_EXCEPTION, exceptionMessage.getExceptionType());
    }


}
