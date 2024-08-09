package com.ewm.service.event;

import com.ewm.util.enums.EventState;
import com.ewm.util.enums.SortEvent;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.NewEventDto;
import dtostorage.main.event.UpdateEventAdminRequest;
import dtostorage.main.event.UpdateEventUserRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateResult;
import dtostorage.main.eventRequest.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto updateEventForAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsForAdmin(Long[] users,
                                         EventState[] states,
                                         Long[] categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size);

    List<EventFullDto> getEventsForPublic(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, SortEvent sortEvent,
                                           Integer from, Integer size);

    EventFullDto getEventForPublic(Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
