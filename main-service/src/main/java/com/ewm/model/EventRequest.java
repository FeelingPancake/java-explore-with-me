package com.ewm.model;

import com.ewm.util.enums.EventRequestStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User requester;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    Event event;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    EventRequestStatus status;
    @Column(name = "created_at")
    LocalDateTime created;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = EventRequestStatus.PENDING;
        }
    }
}

