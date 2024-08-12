package com.ewm.util.entityListeners;

import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Event;
import com.ewm.repository.EventRepository;
import com.ewm.repository.EventRequestRepository;
import com.ewm.util.enums.EventRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventRequestListener {
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;

    @EventListener
    public void handleEventRequestUpdate(Long eventId) {
        Long confirmedCount = eventRequestRepository.countByEventIdAndStatus(eventId, EventRequestStatus.CONFIRMED);
        Event eventEntity = eventRepository.findById(eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId + " нет."));

        eventEntity.setConfirmedRequests(confirmedCount);
        eventRepository.save(eventEntity);
    }
}

