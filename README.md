# spring-boot-consumer
spring boot but with a consumer that works for our code...and a unit test

Take a look at:
- UopKafkaEventConsumer and UopKafkaEventConsumerInitializer - simple example of how to make these testable
- UopKafkaEventConsumerTest - provivdes a nice unit test that make use of logback configuration to test that our exception handling and logging are correct
- logback-test-spring.xml - contains the configuration to make the unit test work.