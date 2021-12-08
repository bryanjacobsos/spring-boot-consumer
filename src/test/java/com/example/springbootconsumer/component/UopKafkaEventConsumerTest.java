package com.example.springbootconsumer.component;

import com.example.springbootconsumer.exception.ExceptionMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.FencedInstanceIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        var exceptionMessageAsString = (String)jsonMap.get("message");

        // Convert the ExceptionMessage into its class/object representation
        var exceptionMessage = mapper.readValue(exceptionMessageAsString, ExceptionMessage.class);

        // should have a valid message to assert against
        assertEquals(UopKafkaEventConsumer.NULL_RECORD_MSG, exceptionMessage.getAdditionalInfo());
    }

    @Test
    public void commitSyncShouldThrowAnException() {
        // TODO: implement this
    }
}
