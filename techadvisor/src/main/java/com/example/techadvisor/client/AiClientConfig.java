package com.example.techadvisor.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AiClientConfig {

    @Bean
    public AiServiceClient aiServiceClient() {
        // 1. Cài đặt địa chỉ Server Python
        RestClient restClient = RestClient.builder()
                .baseUrl("http://ai-service:8000")
                .build();

        // 2. Tự động sinh ra code cho cái Interface của bạn
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(AiServiceClient.class);
    }
}
