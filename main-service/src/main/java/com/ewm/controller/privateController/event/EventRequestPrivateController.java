package com.ewm.controller.privateController.event;

import com.ewm.service.eventRequest.EventRequestService;
import dtostorage.main.eventRequest.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class EventRequestPrivateController {
    private final EventRequestService eventRequestService;


    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationRequest(@PathVariable(name = "userId") Long userId) {
        return eventRequestService.getEventRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createParticipationRequest(@PathVariable(name = "userId") Long userId,
                                                              @RequestParam(name = "eventId") Long eventId) {

        return eventRequestService.createEventRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable(name = "userId") Long userId,
                                                              @PathVariable(name = "requestId") Long requestId) {
        // Можно ли отменить уже отмененной событие или сделать запрос на участие на уже отмененное событие???
        return eventRequestService.cancelEventRequest(userId, requestId);
    }

}
