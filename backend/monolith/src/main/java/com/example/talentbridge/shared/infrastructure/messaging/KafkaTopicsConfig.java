package com.example.talentbridge.shared.infrastructure.messaging;

import com.example.talentbridge.events.Topics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

/** The topics this application writes to, created on start-up when the relay is on (an existing topic is kept). */
@Configuration
@ConditionalOnProperty(prefix = "app.events.relay", name = "enabled", havingValue = "true")
public class KafkaTopicsConfig {

    @Bean
    public NewTopic notificationsTopic(@Value("${app.events.partitions:3}") int partitions,
                                       @Value("${app.events.replicas:1}") short replicas) {
        return TopicBuilder.name(Topics.NOTIFICATIONS).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic realtimeTopic(@Value("${app.events.partitions:3}") int partitions,
                                  @Value("${app.events.replicas:1}") short replicas) {
        return TopicBuilder.name(Topics.REALTIME).partitions(partitions).replicas(replicas).build();
    }
}
