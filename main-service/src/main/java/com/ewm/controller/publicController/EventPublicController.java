package com.ewm.controller.publicController;

import com.ewm.service.event.EventService;
import com.ewm.util.enums.SortEvent;
import dtostorage.main.event.EventFullDto;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "paid", required = false) Boolean paid,
                                        @RequestParam(name = "rangeStart", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(name = "location", required = false) Long locationId,
                                        @RequestParam(name = "onlyAvailable", defaultValue = "false")
                                        Boolean onlyAvailable,
                                        @RequestParam(name = "sort", required = false) SortEvent sort,
                                        @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new IllegalArgumentException("rangeEnd должен быть больше rangeStart");
        }

        return eventService.getEventsForPublic(text, categories, paid, rangeStart,
            rangeEnd, locationId, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventForPublic(@PathVariable(name = "id") Long id) {
        return eventService.getEventForPublic(id);
    }
}
