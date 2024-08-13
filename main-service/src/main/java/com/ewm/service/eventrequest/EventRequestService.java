package com.ewm.service.eventrequest;

import com.ewm.model.EventRequest;
import com.ewm.util.enums.EventRequestStatus;
import dtostorage.main.eventRequest.ParticipationRequestDto;

import java.util.List;

public interface EventRequestService {
    List<ParticipationRequestDto> getEventRequests(Long userId);

    ParticipationRequestDto createEventRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelEventRequest(Long userId, Long requestId);

    List<EventRequest> getEventRequest(Long eventId, EventRequestStatus status);
}
