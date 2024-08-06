package com.ewm.util.entityListeners;

import com.ewm.repository.EventRepository;
import com.ewm.repository.EventRequestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityListenerConfig {

    @Bean
    public EventRequestListener eventRequestListener(EventRepository eventRepository, EventRequestRepository eventRequestRepository) {
        return new EventRequestListener(eventRepository, eventRequestRepository);
    }
}
