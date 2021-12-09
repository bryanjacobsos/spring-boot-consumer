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


public class AbstractConsumerTest {

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
    public void force_poll_to_throw_exception_verify_exception_handing_and_logging() throws IOException {

        var consumer = new UopKafkaEventConsumer();

        // create mock
        var mockKafkaConsumer = mock(KafkaConsumer.class);

        // mock out poll method to throw a possible exception from that method
        when(mockKafkaConsumer.poll(any(Duration.class))).thenThrow(new FencedInstanceIdException("fenced"));

        // assign mock
        consumer.kafkaConsumer = mockKafkaConsumer;

        // invoked the method so that exception handling will get invoked
        consumer.pollConsumer();

        // Logback is configured to write to a file on the file system...read that logged message from the log file
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
    public void force_commitSync_to_throw_exception_verify_exception_handling_and_logging() throws IOException {
        var uopKafkaEventConsumer = new UopKafkaEventConsumer();

        // create mock
        var mockKafkaConsumer = mock(KafkaConsumer.class);

        // this time the poll method needs to run without an exception so need to give it all the stuff it needs
        var records = List.of(new ConsumerRecord<>("topic", 1, 1, "key", "value"));

        var map = Map.of(new TopicPartition("topic", 1), records);

        var consumerRecords = new ConsumerRecords<>(map);

        when(mockKafkaConsumer.poll(any(Duration.class))).thenReturn(consumerRecords);

        // make the commitAsync throw the timeoutexception
        doThrow(new TimeoutException("too long")).when(mockKafkaConsumer).commitSync();

        // assign mock consumer
        uopKafkaEventConsumer.kafkaConsumer = mockKafkaConsumer;

        // now we poll but this time commitAsync is configured to throw an exception
        uopKafkaEventConsumer.pollConsumer();

        // Logback is configured to write to a file on the file system...read that logged message from the log file
        var fileContents = Files.readString(Path.of(file.toURI()));

        // check that the Exception Message Delimiter is present in the message
        assertTrue(fileContents.contains(EXCEPTION_MESSAGE_DELIMITER));

        // the entire log message is a json object so simply convert that to a java map
        var jsonMap = mapper.readValue(fileContents, Map.class);

        // the message property in the json contains ExceptionMessage as json...but it also contains the delimiter
        var exceptionMessageWithDelimiter = (String) jsonMap.get("message");

        // remove the delimiter and convert to an ExceptionMessage
        var exceptionMessage = toExceptionMessageRemoveDelimiter(exceptionMessageWithDelimiter);

        // now we can assert whatever we are interested in proving
        assertEquals(UNEXPECTED_EXCEPTION, exceptionMessage.getExceptionType());
    }
}
