package com.example.springbootconsumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class AppConfig {

    /**
     * the story contains additional configurations for the consumer be sure to notice those...
     * @return
     */
    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        var props = new Properties();

        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // config should be handled as ConfigProps...this should be in application.properties
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");

        // need to set to false so we control commit...probably ok to hardcode this since it's a requirement
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // configured using our serializers
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        return new KafkaConsumer<>(props);
    }

}
