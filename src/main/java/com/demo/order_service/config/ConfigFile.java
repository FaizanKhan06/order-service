package com.demo.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import com.demo.order_service.constant.AppConstant;

@Configuration
public class ConfigFile {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public NewTopic topic(){
        return TopicBuilder.name(AppConstant.TOPIC_ORDERS).build();
    }
}
