package com.ewm.util.mapper.event;

import com.ewm.model.Category;
import com.ewm.model.Event;
import com.ewm.util.enums.EventState;
import com.ewm.util.mapper.DateMapper;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.EventShortDto;
import dtostorage.main.event.NewEventDto;
import dtostorage.main.event.UpdateEventAdminRequest;
import dtostorage.main.event.UpdateEventUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "date", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "views", source = "views")
    EventShortDto toEventShortDto(Event event, Long views);

    @Mapping(target = "location", expression = "java(newEventDto.getLocation() != null ? new Point(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()) : null)")
    @Mapping(target = "paid", expression = "java(newEventDto.getPaid() != null && newEventDto.getPaid())")
    @Mapping(target = "participantLimit", expression = "java(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit())")
    @Mapping(target = "requestModeration", expression = "java(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())")
    @Mapping(target = "category", source = "category")
    Event toEvent(NewEventDto newEventDto, Category category);

    @Mapping(target = "views", source = "views")
    @Mapping(target = "location.lat", source = "event.location.x")
    @Mapping(target = "location.lon", source = "event.location.y")
    EventFullDto toEventFullDto(Event event, Long views);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "updateEventUserRequest.stateAction", qualifiedByName = "stateFromString")
    @Mapping(target = "location", expression = "java(updateEventUserRequest.getLocation() != null ? new Point(updateEventUserRequest.getLocation().getLat(), updateEventUserRequest.getLocation().getLon()) : null)")
    Event toEvent(UpdateEventUserRequest updateEventUserRequest, Category category);

    @Mapping(target = "state", source = "updateEventAdminRequest.stateAction", qualifiedByName = "stateFromString")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", expression = "java(updateEventAdminRequest.getLocation() != null ? new Point(updateEventAdminRequest.getLocation().getLat(), updateEventAdminRequest.getLocation().getLon()) : null)")
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Category category);

    @Named("stateFromString")
    default EventState stateFromString(String stateAction) {
        if (stateAction == null) {
            return null;
        }

        switch (stateAction) {
            case "SEND_TO_REVIEW":
                return EventState.PENDING;
            case "CANCEL_REVIEW":
            case "REJECT_EVENT":
                return EventState.CANCELED;
            case "PUBLISH_EVENT":
                return EventState.PUBLISHED;
            default:
                return null;
        }
    }
}

