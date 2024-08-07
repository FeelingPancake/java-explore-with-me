package com.ewm.util.mapper.event;

import com.ewm.model.EventRequest;
import com.ewm.util.enums.EventRequestStatus;
import dtostorage.main.eventRequest.EventRequestStatusUpdateResult;
import dtostorage.main.eventRequest.ParticipationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {
    EventRequestMapper INSTANCE = Mappers.getMapper(EventRequestMapper.class);

    @Mapping(target = "requester", source = "eventRequest.requester.id")
    @Mapping(target = "event", source = "eventRequest.event.id")
    @Mapping(target = "status", source = "eventRequest.status")
    ParticipationRequestDto toParticipationRequestDto(EventRequest eventRequest);

    default String mapStatus(EventRequestStatus status) {
        return status.toString();
    }

    default EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<EventRequest> eventRequests) {
        List<ParticipationRequestDto> confirmedRequests = eventRequests.stream()
            .filter(eventRequest -> eventRequest.getStatus() == EventRequestStatus.CONFIRMED)
            .map(this::toParticipationRequestDto)
            .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = eventRequests.stream()
            .filter(eventRequest -> eventRequest.getStatus() == EventRequestStatus.CANCELED)
            .map(this::toParticipationRequestDto)
            .peek(dto -> dto.setStatus("REJECTED"))
            .collect(Collectors.toList());

        return EventRequestStatusUpdateResult.builder()
            .confirmedRequests(confirmedRequests)
            .rejectedRequests(rejectedRequests)
            .build();
    }

}
