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

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "point", expression = "java(newEventDto.getLocation() != null ? new Point(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon()) : null)")
    @Mapping(target = "paid", expression = "java(newEventDto.getPaid() != null && newEventDto.getPaid())")
    @Mapping(target = "participantLimit", expression = "java(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit())")
    @Mapping(target = "requestModeration", expression = "java(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "id", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category);

    @Mapping(target = "views", source = "views")
    @Mapping(target = "location.lat", source = "event.point.x")
    @Mapping(target = "location.lon", source = "event.point.y")
    EventFullDto toEventFullDto(Event event, Long views);


    @Mapping(target = "state", expression = "java(stateFromString(updateEventUserRequest.getStateAction()) == null ? existingEvent.getState() : stateFromString(updateEventUserRequest.getStateAction()))")
    @Mapping(target = "annotation", expression = "java(updateEventUserRequest.getAnnotation() == null ? existingEvent.getAnnotation() : updateEventUserRequest.getAnnotation())")
    @Mapping(target = "category", expression = "java(category == null ? existingEvent.getCategory() : category)")
    @Mapping(target = "description", expression = "java(updateEventUserRequest.getDescription() == null ? existingEvent.getDescription() : updateEventUserRequest.getDescription())")
    @Mapping(target = "eventDate", expression = "java(updateEventUserRequest.getEventDate() == null ? existingEvent.getEventDate() : updateEventUserRequest.getEventDate())")
    @Mapping(target = "point", expression = "java(updateEventUserRequest.getLocation() == null ? existingEvent.getPoint() : new Point(updateEventUserRequest.getLocation().getLat(), updateEventUserRequest.getLocation().getLon()))")
    @Mapping(target = "paid", expression = "java(updateEventUserRequest.getPaid() == null ? existingEvent.getPaid() : updateEventUserRequest.getPaid())")
    @Mapping(target = "participantLimit", expression = "java(updateEventUserRequest.getParticipantLimit() == null ? existingEvent.getParticipantLimit() : updateEventUserRequest.getParticipantLimit())")
    @Mapping(target = "requestModeration", expression = "java(updateEventUserRequest.getRequestModeration() == null ? existingEvent.getRequestModeration() : updateEventUserRequest.getRequestModeration())")
    @Mapping(target = "title", expression = "java(updateEventUserRequest.getTitle() == null ? existingEvent.getTitle() : updateEventUserRequest.getTitle())")
    @Mapping(target = "id", source = "existingEvent.id")
    Event toEvent(UpdateEventUserRequest updateEventUserRequest, Event existingEvent, Category category);

    @Mapping(target = "state", expression = "java(stateFromString(updateEventAdminRequest.getStateAction()) == null ? existingEvent.getState() : stateFromString(updateEventAdminRequest.getStateAction()))")
    @Mapping(target = "annotation", expression = "java(updateEventAdminRequest.getAnnotation() == null ? existingEvent.getAnnotation() : updateEventAdminRequest.getAnnotation())")
    @Mapping(target = "category", expression = "java(category == null ? existingEvent.getCategory() : category)")
    @Mapping(target = "description", expression = "java(updateEventAdminRequest.getDescription() == null ? existingEvent.getDescription() : updateEventAdminRequest.getDescription())")
    @Mapping(target = "eventDate", expression = "java(updateEventAdminRequest.getEventDate() == null ? existingEvent.getEventDate() : updateEventAdminRequest.getEventDate())")
    @Mapping(target = "point", expression = "java(updateEventAdminRequest.getLocation() == null ? existingEvent.getPoint() : new Point(updateEventAdminRequest.getLocation().getLat(), updateEventAdminRequest.getLocation().getLon()))")
    @Mapping(target = "paid", expression = "java(updateEventAdminRequest.getPaid() == null ? existingEvent.getPaid() : updateEventAdminRequest.getPaid())")
    @Mapping(target = "participantLimit", expression = "java(updateEventAdminRequest.getParticipantLimit() == null ? existingEvent.getParticipantLimit() : updateEventAdminRequest.getParticipantLimit())")
    @Mapping(target = "requestModeration", expression = "java(updateEventAdminRequest.getRequestModeration() == null ? existingEvent.getRequestModeration() : updateEventAdminRequest.getRequestModeration())")
    @Mapping(target = "title", expression = "java(updateEventAdminRequest.getTitle() == null ? existingEvent.getTitle() : updateEventAdminRequest.getTitle())")
    @Mapping(target = "id", source = "existingEvent.id")
    Event toEvent(UpdateEventAdminRequest updateEventAdminRequest, Event existingEvent, Category category);

    @Named("stateFromString")
    default EventState stateFromString(String stateAction) {
        if (stateAction == null) {
            return null;
        }

        return switch (stateAction) {
            case "SEND_TO_REVIEW" -> EventState.PENDING;
            case "CANCEL_REVIEW", "REJECT_EVENT" -> EventState.CANCELED;
            case "PUBLISH_EVENT" -> EventState.PUBLISHED;
            default -> null;
        };
    }
}

