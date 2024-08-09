package com.ewm.repository;

import com.ewm.model.EventRequest;
import com.ewm.util.enums.EventRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findByEventId(Long eventId);

    List<EventRequest> findByRequesterId(Long requesterId);

    List<EventRequest> findByEventIdAndStatus(Long eventId, EventRequestStatus status);

    Long countByEventIdAndStatus(Long eventId, EventRequestStatus status);

    Optional<EventRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);
}
