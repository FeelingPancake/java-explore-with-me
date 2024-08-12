package com.ewm.controller.privateController.event;

import com.ewm.service.event.EventService;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.NewEventDto;
import dtostorage.main.event.UpdateEventUserRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateResult;
import dtostorage.main.eventRequest.ParticipationRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getEvents(@PathVariable(name = "userId") Long userId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") Long userId,
                                    @RequestBody @Valid @NotNull NewEventDto newEventDto) {


        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "userId") Long userId,
                                 @PathVariable(name = "eventId") Long eventId) {

        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "eventId") Long eventId,
                                    @RequestBody @NotNull @Valid UpdateEventUserRequest updateEventUserRequest) {

        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable(name = "userId") Long userId,
                                                                  @PathVariable(name = "eventId") Long eventId) {

        return eventService.getRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequests(@PathVariable(name = "userId") Long userId,
                                                              @PathVariable(name = "eventId") Long eventId,
                                                              @RequestBody @Valid
                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        return eventService.updateRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
