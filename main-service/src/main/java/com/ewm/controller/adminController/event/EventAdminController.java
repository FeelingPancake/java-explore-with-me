package com.ewm.controller.adminController.event;

import com.ewm.service.event.EventService;
import com.ewm.util.enums.EventState;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.UpdateEventAdminRequest;
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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) Long[] users,
                                        @RequestParam(name = "states", required = false) EventState[] states,
                                        @RequestParam(name = "categories", required = false) Long[] categories,
                                        @RequestParam(name = "rangeStart", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new IllegalArgumentException("rangeEnd должен быть больше rangeStart");
        }

        return eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId, @RequestBody @Valid
    UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEventForAdmin(eventId, updateEventAdminRequest);
    }
}
