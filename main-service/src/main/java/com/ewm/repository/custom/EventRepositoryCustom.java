package com.ewm.repository.custom;

import com.ewm.model.Event;
import com.ewm.util.enums.EventState;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> getEventsForAdmin(Long[] users, EventState[] states, Long[] categories, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Pageable pageable);

    List<Event> getEventForPublic(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);
}
