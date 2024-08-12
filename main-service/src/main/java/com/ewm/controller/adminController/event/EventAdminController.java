package com.ewm.controller.adminController.event;

import com.ewm.service.event.EventService;
import com.ewm.util.enums.EventState;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.UpdateEventAdminRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                        @RequestParam(name = "states", required = false) List<EventState> states,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "rangeStart", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(name = "location", required = false) Long locationId,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new IllegalArgumentException("rangeEnd должен быть больше rangeStart");
        }

        return eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, locationId, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventForAdmin(eventId, updateEventAdminRequest);
    }
}
