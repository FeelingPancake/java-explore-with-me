package com.ewm.util.entityListeners;

import com.ewm.model.Event;
import com.ewm.model.EventRequest;
import com.ewm.repository.EventRepository;
import com.ewm.repository.EventRequestRepository;
import com.ewm.util.enums.EventRequestStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

@Transactional
public class EventRequestListener {

    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;

    public EventRequestListener(EventRepository eventRepository, EventRequestRepository eventRequestRepository) {
        this.eventRepository = eventRepository;
        this.eventRequestRepository = eventRequestRepository;
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    public void updateConfirmedRequests(EventRequest eventRequest) {
        Event event = eventRequest.getEvent();
        Long confirmedCount =
            eventRequestRepository.countByEventIdAndStatus(event.getId(), EventRequestStatus.CONFIRMED);
        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);
    }
}

