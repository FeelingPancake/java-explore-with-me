package com.ewm.service.eventRequest;

import com.ewm.exception.AlreadyExistsExeption;
import com.ewm.exception.ConfilctException;
import com.ewm.exception.NotExistsExeption;
import com.ewm.model.Event;
import com.ewm.model.EventRequest;
import com.ewm.model.User;
import com.ewm.repository.EventRepository;
import com.ewm.repository.EventRequestRepository;
import com.ewm.repository.UserRepository;
import com.ewm.util.enums.EventRequestStatus;
import com.ewm.util.mapper.event.EventRequestMapper;
import dtostorage.main.eventRequest.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventRequestServiceImpl implements EventRequestService {
    private final EventRequestRepository eventRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRequestMapper eventRequestMapper = EventRequestMapper.INSTANCE;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId) {
        List<EventRequest> event = eventRequestRepository.findByRequesterId(userId);

        return event.stream().map(eventRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }


    @Override
    public ParticipationRequestDto createEventRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
            new NotExistsExeption("События - " + eventId + " нет."));

        User user = userRepository.findById(userId).orElseThrow(() ->
            new NotExistsExeption("Пользователя - " + userId + " нет.")
        );

        EventRequest eventRequest = EventRequest.builder()
            .requester(user)
            .created(LocalDateTime.now())
            .event(event)
            .status(EventRequestStatus.PENDING)
            .build();

        if (eventRequestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new AlreadyExistsExeption("Запрос уже существует");
        }

        if (event.getInitiator().equals(user)) {
            throw new ConfilctException("Инициатор не может отправить запрос на свое событие");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConfilctException("Достигнут лимит запросов на участие");
        }

        if (!event.getRequestModeration()) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
        }

        return eventRequestMapper.toParticipationRequestDto(eventRequestRepository.save(eventRequest));
    }

    @Override
    public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
        EventRequest eventRequest = eventRequestRepository.findById(requestId).orElseThrow(() ->
            new NotExistsExeption("Запроса на участие - " + requestId + " нет."));

        User user = userRepository.findById(userId).orElseThrow(() ->
            new NotExistsExeption("Пользователя - " + userId + " нет.")
        );

        if (!eventRequest.getRequester().equals(user)) {
            throw new ConfilctException("Пользователь " + userId + "не является тем, кто создал запрос");
        }

        eventRequest.setStatus(EventRequestStatus.REJECTED);

        return eventRequestMapper.toParticipationRequestDto(eventRequestRepository.save(eventRequest));
    }

    @Override
    public List<EventRequest> getEventRequest(Long eventId, EventRequestStatus status) {

        return eventRequestRepository.findByEventIdAndStatus(eventId, status);
    }
}
