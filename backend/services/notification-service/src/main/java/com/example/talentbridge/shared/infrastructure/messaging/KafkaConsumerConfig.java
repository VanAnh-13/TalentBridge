package com.example.talentbridge.shared.infrastructure.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * A listener that throws (e.g. the database is briefly away) gets the same record again 5 times, a second apart, and
 * then it is logged and skipped so one bad record cannot block its partition forever.
 */
@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                (record, ex) -> log.error("Giving up on {}-{}@{}: {}", record.topic(), record.partition(), record.offset(),
                        ex.getMessage()),
                new FixedBackOff(1000L, 5));
        handler.addNotRetryableExceptions(IllegalArgumentException.class);
        return handler;
    }
}
