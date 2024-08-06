package com.ewm.service.event;

import com.ewm.exception.ConfilctException;
import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Category;
import com.ewm.model.Event;
import com.ewm.model.EventRequest;
import com.ewm.repository.CategoryRepository;
import com.ewm.repository.EventRepository;
import com.ewm.repository.EventRequestRepository;
import com.ewm.util.enums.EventRequestStatus;
import com.ewm.util.enums.EventState;
import com.ewm.util.enums.SortEvent;
import com.ewm.util.mapper.event.EventMapper;
import com.ewm.util.mapper.event.EventRequestMapper;
import com.ewm.util.stats.StatsClient;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.EventShortDto;
import dtostorage.main.event.NewEventDto;
import dtostorage.main.event.UpdateEventAdminRequest;
import dtostorage.main.event.UpdateEventUserRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateResult;
import dtostorage.main.eventRequest.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;
    private final StatsClient statsClient;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper = EventMapper.INSTANCE;
    private final EventRequestMapper eventRequestMapper = EventRequestMapper.INSTANCE;


    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        log.debug("Вызов getEvents() с параметрами userId: {}, from: {}, size: {}", userId, from, size);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).toList();
        HashMap<Long, Long> views = statsClient.getMassStats(events);

        log.debug("getEvents() возвращает: {}", events);
        return events.stream().map(event -> eventMapper.toEventShortDto(event, views.getOrDefault(event.getId(), 0L)))
            .collect(
                Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsForPublic(String text, Long[] categories, Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, Boolean onlyAvailable, SortEvent sort,
                                                  Integer from, Integer size) {
        log.debug("Вызов getEventsForPublic() с параметрами text: {}," +
                " categories: {}, paid: {}, rangeStart: {}," +
                " rangeEnd: {}, onluAvailable: {}. from: {}, size: {}", text, categories, paid, rangeStart,
            rangeEnd, onlyAvailable, from, size);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        List<Event> events =
            eventRepository.getEventForPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);

        log.debug("getEventsForPublic() возвращает: {}", events);
        HashMap<Long, Long> views = statsClient.getMassStats(events);

        Comparator<EventShortDto> comparator;
        if (sort == null) {
            comparator = Comparator.comparing(EventShortDto::getId);
        } else {
            switch (sort) {
                case EVENT_DATE:
                    comparator = Comparator.comparing(EventShortDto::getDate);
                    break;
                case VIEWS:
                    comparator = Comparator.comparing(EventShortDto::getViews);
                    break;
                default:
                    comparator = Comparator.comparing(EventShortDto::getId);
            }
        }

        return events.stream()
            .map(event -> eventMapper.toEventShortDto(event, views.getOrDefault(event.getId(), 0L)))
            .sorted(comparator)
            .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(Long[] users, EventState[] states, Long[] categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info(
            "getEventsForAdmin - users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
            Arrays.toString(users), Arrays.toString(states), Arrays.toString(categories), rangeStart, rangeEnd, from,
            size);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventDate"));

        log.debug("Pageable - {}", pageable);

        List<Event> events =
            eventRepository.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        log.debug("Events -: {}", events);

        HashMap<Long, Long> views = statsClient.getMassStats(events);

        log.debug("Views -: {}", views);

        List<EventFullDto> eventFullDtos = events.stream()
            .map(event -> eventMapper.toEventFullDto(event, views.getOrDefault(event.getId(), 0L)))
            .collect(Collectors.toList());

        log.info("Возврат {} events", eventFullDtos.size());

        return eventFullDtos;
    }

    @Override
    public EventFullDto updateEventForAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Вызов updateEventForAdmin() с параметрами eventId: {}", eventId);

        Event existingEvent = eventRepository.findById(eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId + " нет."));

        Category category = categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(
            () -> new NotExistsExeption("Категории - " + updateEventAdminRequest.getCategory() + " нет."));

        Event event = eventMapper.toEvent(updateEventAdminRequest, category);
        Event updatedEvent = existingEvent.toBuilder()
            .annotation(event.getAnnotation() == null ? existingEvent.getAnnotation() : event.getAnnotation())
            .category(event.getCategory() == null ? existingEvent.getCategory() : event.getCategory())
            .description(event.getDescription() == null ? existingEvent.getDescription() : event.getDescription())
            .eventDate(event.getEventDate() == null ? existingEvent.getEventDate() : event.getEventDate())
            .location(event.getLocation() == null ? existingEvent.getLocation() : event.getLocation())
            .paid(event.getPaid() == null ? existingEvent.getPaid() : event.getPaid())
            .participantLimit(
                event.getParticipantLimit() == null ? existingEvent.getParticipantLimit() : event.getParticipantLimit())
            .requestModeration(event.getRequestModeration() == null ? existingEvent.getRequestModeration() :
                event.getRequestModeration())
            .state(event.getState() == null ? existingEvent.getState() : event.getState())
            .title(event.getTitle() == null ? existingEvent.getTitle() : event.getTitle())
            .build();


        Event savedEvent = eventRepository.save(updatedEvent);
        HashMap<Long, Long> views = statsClient.getMassStats(List.of(savedEvent));

        return eventMapper.toEventFullDto(savedEvent, views.getOrDefault(savedEvent.getId(), 0L));
    }


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.debug("Вызов createEvent() с параметрами userId: {}, event: {}", userId, newEventDto);
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
            () -> new NotExistsExeption("Категории - " + newEventDto.getCategory() + " нет.")
        );
        Event event = eventMapper.toEvent(newEventDto, category);
        Long views = 0L;
        Event createdEvent = eventRepository.save(event);

        log.debug("createEvent() создано: {}", createdEvent);
        return eventMapper.toEventFullDto(createdEvent, views);
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.debug("Вызов getEvent() с параметрами userId: {}, eventId: {}", userId, eventId);

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId.toString() + " нет."));
        HashMap<Long, Long> views = statsClient.getMassStats(List.of(event));

        log.debug("getEvent() возвращает: {}", event);
        return eventMapper.toEventFullDto(event, views.getOrDefault(eventId, 0L));
    }

    @Override
    public EventFullDto getEventForPublic(Long eventId) {
        log.debug("Вызов getEventForPublic() с параметрами eventId: {}", eventId);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId.toString() + " нет."));

        HashMap<Long, Long> views = statsClient.getMassStats(List.of(event));

        log.debug("getEventForPublic() возвращает: {}", event);
        return eventMapper.toEventFullDto(event, views.getOrDefault(eventId, 0L));
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Вызов updateEvent() с параметрами userId: {}, eventId: {}, event: {}", userId, eventId,
            updateEventUserRequest);

        Event existingEvent = eventRepository.findById(eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId + " нет."));

        Category category = updateEventUserRequest.getCategory() == null ? null :
            categoryRepository.findById(updateEventUserRequest.getCategory()).orElseThrow(
                () -> new NotExistsExeption("Категории - " + updateEventUserRequest.getCategory() + " нет.")
            );
        Event event = eventMapper.toEvent(updateEventUserRequest, category);

        Event eventToUpdate = existingEvent.toBuilder()
            .annotation(event.getAnnotation() == null ? existingEvent.getAnnotation() : event.getAnnotation())
            .category(event.getCategory() == null ? existingEvent.getCategory() : event.getCategory())
            .description(event.getDescription() == null ? existingEvent.getDescription() : event.getDescription())
            .eventDate(event.getEventDate() == null ? existingEvent.getEventDate() : event.getEventDate())
            .location(event.getLocation() == null ? existingEvent.getLocation() : event.getLocation())
            .paid(event.getPaid() == null ? existingEvent.getPaid() : event.getPaid())
            .participantLimit(
                event.getParticipantLimit() == null ? existingEvent.getParticipantLimit() : event.getParticipantLimit())
            .requestModeration(event.getRequestModeration() == null ? existingEvent.getRequestModeration() :
                event.getRequestModeration())
            .state(event.getState() == null ? existingEvent.getState() : event.getState())
            .title(event.getTitle() == null ? existingEvent.getTitle() : event.getTitle())
            .build();
        HashMap<Long, Long> views = statsClient.getMassStats(List.of(eventToUpdate));
        Event updatedEvent = eventRepository.save(eventToUpdate);

        log.debug("updateEvent() обновлено: {}", updatedEvent);
        return eventMapper.toEventFullDto(updatedEvent, views.getOrDefault(updatedEvent.getId(), 0L));
    }

    @Override
    public List<ParticipationRequestDto> getRequest(Long userId, Long eventId) {
        log.debug("Вызов getRequest() с параметрами userId: {}, eventId: {}", userId, eventId);

        if (eventRepository.findByInitiatorIdAndId(userId, eventId).isPresent()) {
            List<EventRequest> requests = eventRequestRepository.findByEventId(eventId);
            log.debug("getRequest() возвращает: {}", requests);
            return requests.stream().map(eventRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
        }

        log.debug("getRequest() возвращает пустой список");
        return Collections.emptyList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug(
            "Вызов updateRequestsStatus() с параметрами userId: {}, eventId: {}, eventRequestStatusUpdateRequest: {}",
            userId, eventId, eventRequestStatusUpdateRequest);

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId.toString() + " нет."));

        List<EventRequest> pendingRequests =
            eventRequestRepository.findByEventIdAndStatus(eventId, EventRequestStatus.PENDING);
        Integer limit = event.getParticipantLimit();
        Long confirmedRequests = eventRequestRepository.countByEventIdAndStatus(eventId, EventRequestStatus.CONFIRMED);

        if (limit > 0 && confirmedRequests >= limit) {
            log.warn("updateRequestsStatus() лимит заявок превышен для eventId: {}", eventId);
            throw new ConfilctException("Лимит заявок превышен");
        }

        List<EventRequest> requestsToUpdate = new ArrayList<>();

        for (EventRequest eventRequest : pendingRequests) {
            if (Enum.valueOf(EventRequestStatus.class, eventRequestStatusUpdateRequest.getStatus()) ==
                EventRequestStatus.CONFIRMED) {
                if (limit > 0 && confirmedRequests >= limit) {
                    eventRequest.setStatus(EventRequestStatus.REJECTED);
                    requestsToUpdate.add(eventRequest);
                } else {
                    eventRequest.setStatus(EventRequestStatus.CONFIRMED);
                    requestsToUpdate.add(eventRequest);
                    confirmedRequests++;
                }
            }
        }

        List<EventRequest> savedRequest = eventRequestRepository.saveAll(requestsToUpdate);

        log.debug("updateRequestsStatus() обновленные заявки: {}", savedRequest);
        return eventRequestMapper.toEventRequestStatusUpdateResult(savedRequest);
    }
}
