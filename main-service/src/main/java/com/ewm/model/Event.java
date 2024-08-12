package com.ewm.model;

import com.ewm.util.enums.EventState;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "annotation")
    String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;
    @Column(name = "confirmed_requests")
    Long confirmedRequests;
    @Column(name = "created_on")
    LocalDateTime createdOn;
    @Column(name = "description")
    String description;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User initiator;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "location_x")),
        @AttributeOverride(name = "y", column = @Column(name = "location_y"))
    })
    Point point;
    @Column(name = "paid")
    Boolean paid;
    @Column(name = "event_limit")
    Integer participantLimit;
    @Column(name = "published_on")
    LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    EventState state;
    @Column(name = "title")
    String title;
    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;

    @PrePersist
    protected void onCreate() {
        if (this.requestModeration == null) {
            requestModeration = true;
        }
        participantLimit = 0;
        confirmedRequests = 0L;
        state = EventState.PENDING;
        createdOn = LocalDateTime.now();
    }
}
