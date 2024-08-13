package com.ewm.repository.custom;

import com.ewm.model.Event;
import com.ewm.model.Location;
import com.ewm.util.enums.EventState;
import com.ewm.util.geo.GeoUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> getEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Location location, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in((states)));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in((categories)));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        TypedQuery<Event> resultQuery = entityManager.createQuery(query);

        resultQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        resultQuery.setMaxResults(pageable.getPageSize());

        List<Event> events = resultQuery.getResultList();

        if (location != null) {
            return events.stream()
                .filter(event -> GeoUtil.isWithinRadius(event.getPoint().getX(), event.getPoint().getY(), location))
                .collect(Collectors.toList());
        } else {
            return events;
        }
    }

    @Override
    public List<Event> getEventForPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Location location, Boolean onlyAvailable,
                                         Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (text != null && !text.isBlank()) {
            predicates.add(cb.or(
                cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%")
            ));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            predicates.add(paid ? cb.isTrue(root.get("paid")) : cb.isFalse(root.get("paid")));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (rangeEnd == null && rangeStart == null) {
            predicates.add(cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }

        if (onlyAvailable != null && onlyAvailable) {
            predicates.add(cb.or(cb.lessThanOrEqualTo(root.get("confirmedRequests"), root.get("participantLimit")),
                cb.equal(root.get("participationLimit"), 0)));
        }

        predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));

        TypedQuery<Event> resultQuery = entityManager.createQuery(query);

        resultQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        resultQuery.setMaxResults(pageable.getPageSize());

        List<Event> events = resultQuery.getResultList();

        if (location != null) {
            return events.stream()
                .filter(event -> GeoUtil.isWithinRadius(event.getPoint().getX(), event.getPoint().getY(), location))
                .collect(Collectors.toList());
        } else {
            return events;
        }
    }


}
