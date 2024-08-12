package com.ewm.util.stats;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import statshttpclient.StatsHttpClient;

@Configuration
public class StatsClientConfig {

    @Bean
    public StatsHttpClient statsHttpClient(@Value("${stats-service.url}") String serverUrl,
                                           RestTemplateBuilder builder) {
        return new StatsHttpClient(serverUrl, builder);
    }
}
