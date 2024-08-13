package com.ewm.repository.custom;

import com.ewm.model.Event;
import com.ewm.model.Location;
import com.ewm.util.enums.EventState;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> getEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Location location, Pageable pageable);

    List<Event> getEventForPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Location location, Boolean onlyAvailable,
                                  Pageable pageable);
}
