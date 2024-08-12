package com.ewm.service.event;

import com.ewm.exception.ConfilctException;
import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Category;
import com.ewm.model.Event;
import com.ewm.model.EventRequest;
import com.ewm.model.Location;
import com.ewm.model.User;
import com.ewm.repository.CategoryRepository;
import com.ewm.repository.EventRepository;
import com.ewm.repository.EventRequestRepository;
import com.ewm.repository.LocationRepository;
import com.ewm.repository.UserRepository;
import com.ewm.util.enums.EventRequestStatus;
import com.ewm.util.enums.EventState;
import com.ewm.util.enums.SortEvent;
import com.ewm.util.mapper.event.EventMapper;
import com.ewm.util.mapper.event.EventRequestMapper;
import com.ewm.util.stats.StatsClient;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.NewEventDto;
import dtostorage.main.event.UpdateEventAdminRequest;
import dtostorage.main.event.UpdateEventUserRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateRequest;
import dtostorage.main.eventRequest.EventRequestStatusUpdateResult;
import dtostorage.main.eventRequest.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper = EventMapper.INSTANCE;
    private final EventRequestMapper eventRequestMapper = EventRequestMapper.INSTANCE;


    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(Long userId, Integer from, Integer size) {
        log.debug("Вызов getEvents() с параметрами userId: {}, from: {}, size: {}", userId, from, size);
        User initiator = userRepository.findById(userId).orElseThrow(
            () -> new NotExistsExeption("Пользователя - " + userId + " нет")
        );

        Pageable pageable = createPageable(from, size);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).toList();
        HashMap<Long, Long> views = statsClient.getMassStats(events);

        log.debug("getEvents() возвращает: {}", events);
        return events.stream().map(event -> eventMapper.toEventFullDto(event, views.getOrDefault(event.getId(), 0L)))
            .collect(
                Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd, Long locationId, Boolean onlyAvailable,
                                                 SortEvent sort,
                                                 Integer from, Integer size) {
        log.debug("Вызов getEventsForPublic() с параметрами text: {}," +
                " categories: {}, paid: {}, rangeStart: {}," +
                " rangeEnd: {}, onluAvailable: {}. from: {}, size: {} ", text, categories, paid, rangeStart,
            rangeEnd, onlyAvailable, from, size);

        Pageable pageable = createPageable(from, size);

        Location location = locationRepository.findById(locationId).orElseThrow(
            () -> new NotExistsExeption("Локации с " + locationId + " нет")
        );
        log.debug("getEventsForPublic Получен локация - {}", location);

        List<Event> events =
            eventRepository.getEventForPublic(text, categories, paid, rangeStart, rangeEnd, location, onlyAvailable,
                pageable);

        log.debug("getEventsForPublic() возвращает: {}", events);
        HashMap<Long, Long> views = statsClient.getMassStats(events);

        Comparator<EventFullDto> comparator = getComparator(sort);

        return events.stream()
            .map(event -> eventMapper.toEventFullDto(event, views.getOrDefault(event.getId(), 0L)))
            .sorted(comparator)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, Long locationId, Integer from, Integer size) {
        log.info(
            "getEventsForAdmin - users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, locationId {}, from: {}, size: {}",
            users, states, categories, rangeStart, rangeEnd,
            locationId, from,
            size);

        Pageable pageable = createPageable(from, size);

        log.debug("Pageable - {}", pageable);

        Location location = locationRepository.findById(locationId).orElseThrow(
            () -> new NotExistsExeption("Локации с " + locationId + " нет")
        );
        log.debug("getEventsForAdminПолучен локация - {}", location);

        List<Event> events =
            eventRepository.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, location, pageable);

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

        Category category = updateEventAdminRequest.getCategory() == null ? null :
            categoryRepository.findById(updateEventAdminRequest.getCategory()).orElseThrow(
                () -> new NotExistsExeption("Категории - " + updateEventAdminRequest.getCategory() + " нет."));

        Event updatedEvent = eventMapper.toEvent(updateEventAdminRequest, existingEvent, category);

        log.debug("updatedEvent - {}, existingEvent - {}", updatedEvent, existingEvent);
        EventState existingState = existingEvent.getState();
        EventState updatedState = updatedEvent.getState();
        if (existingState.equals(updatedState) &&
            updatedState.equals(EventState.PUBLISHED)) {
            throw new ConfilctException("Публикация уже опубликованного события");
        } else if (existingState.equals(EventState.CANCELED) && updatedState.equals(EventState.PUBLISHED)) {
            throw new ConfilctException("Публикация отменного события");
        } else if (existingState.equals(EventState.PUBLISHED) && updatedState.equals(EventState.CANCELED)) {
            throw new ConfilctException("Отмена опубликованного события");
        }

        if (updatedEvent.getState().equals(EventState.PUBLISHED)) {
            updatedEvent.setPublishedOn(LocalDateTime.now());
        }

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
        User initiator = userRepository.findById(userId).orElseThrow(
            () -> new NotExistsExeption("Пользователя с " + userId + " нет."));

        Event event = eventMapper.toEvent(newEventDto, category);
        event.setInitiator(initiator);
        Long views = 0L;
        Event createdEvent = eventRepository.save(event);

        log.debug("createEvent() создано: {} c id - {}", createdEvent, createdEvent.getId());
        return eventMapper.toEventFullDto(createdEvent, views);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.debug("Вызов getEvent() с параметрами userId: {}, eventId: {}", userId, eventId);

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId.toString() + " нет."));
        HashMap<Long, Long> views = statsClient.getMassStats(List.of(event));
        User initiator = userRepository.findById(userId).orElseThrow(
            () -> new NotExistsExeption("Пользователя с " + userId + " нет."));

        if (!event.getInitiator().equals(initiator)) {
            throw new ConfilctException("Событие - " + eventId + "не создана пользователем " + userId);
        }

        log.debug("getEvent() возвращает: {}", event);
        return eventMapper.toEventFullDto(event, views.getOrDefault(eventId, 0L));
    }

    @Override
    @Transactional(readOnly = true)
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

        User initiator = userRepository.findById(userId).orElseThrow(
            () -> new NotExistsExeption("Пользователя с " + userId + " нет."));

        if (!existingEvent.getInitiator().equals(initiator)) {
            throw new ConfilctException("Пользователь не является инициатором события");
        }

        Event eventToUpdate = eventMapper.toEvent(updateEventUserRequest, existingEvent, category);

        HashMap<Long, Long> views = statsClient.getMassStats(List.of(eventToUpdate));

        if (eventToUpdate.getState().equals(EventState.PUBLISHED)) {
            throw new ConfilctException("Опубликовать может только админ");
        }

        Event updatedEvent = eventRepository.save(eventToUpdate);

        log.debug("updateEvent() обновлено: {}", updatedEvent);
        return eventMapper.toEventFullDto(updatedEvent, views.getOrDefault(updatedEvent.getId(), 0L));
    }

    @Override
    @Transactional(readOnly = true)
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
            if (eventRequestStatusUpdateRequest.getStatus().equals("CONFIRMED")) { // в спецификации - это именно строки
                if (limit > 0 && confirmedRequests >= limit) {
                    eventRequest.setStatus(EventRequestStatus.CANCELED);
                    requestsToUpdate.add(eventRequest);
                } else {
                    eventRequest.setStatus(EventRequestStatus.CONFIRMED);
                    requestsToUpdate.add(eventRequest);
                    confirmedRequests++;
                }
            } else {
                eventRequest.setStatus(EventRequestStatus.CANCELED);
                requestsToUpdate.add(eventRequest);
            }
        }

        List<EventRequest> savedRequest = eventRequestRepository.saveAll(requestsToUpdate);
        publisher.publishEvent(eventId);
        log.debug("updateRequestsStatus() обновленные заявки: {}", savedRequest);

        return eventRequestMapper.toEventRequestStatusUpdateResult(savedRequest);
    }

    private Pageable createPageable(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "eventDate"));
    }

    private Comparator<EventFullDto> getComparator(SortEvent sort) {
        if (sort == null) {
            return Comparator.comparing(EventFullDto::getId);
        }

        return switch (sort) {
            case EVENT_DATE -> Comparator.comparing(EventFullDto::getEventDate);
            case VIEWS -> Comparator.comparing(EventFullDto::getViews);
            default -> Comparator.comparing(EventFullDto::getId);
        };
    }
}
