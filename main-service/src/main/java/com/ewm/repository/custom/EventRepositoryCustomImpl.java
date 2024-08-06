package com.ewm.repository.custom;

import com.ewm.model.Event;
import com.ewm.util.enums.EventState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> getEventsForAdmin(Long[] users, EventState[] states, Long[] categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null && users.length > 0) {
            if (!(users.length == 1 && users[0] == 0)) {
                predicates.add(root.get("initiator").in((Object[]) users));
            }
        }

        if (states != null && states.length > 0) {
            predicates.add(root.get("state").in((Object[]) states));
        }

        if (categories != null && categories.length > 0) {
            if (!(categories.length == 1 && categories[0] == 0)) {
                predicates.add(root.get("category").in((Object[]) categories));
            }
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<Event> getEventForPublic(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable,
                                         Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (text != null && !text.isBlank() && !text.equals("0")) {
            predicates.add(cb.or(
                cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%")
            ));
        }

        if (categories != null && categories.length > 0) {
            if (!(categories.length == 1 && categories[0] == 0)) {
                predicates.add(root.get("category").in((Object[]) categories));
            }
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

        return entityManager.createQuery(query).getResultList();
    }


}
