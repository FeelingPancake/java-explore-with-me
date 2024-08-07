package com.ewm.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "title", unique = true, nullable = false)
    String title;
    @Column(name = "pinned")
    Boolean pinned = false;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "compilation_events",
        joinColumns = @JoinColumn(name = "compilation_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    List<Event> events;

    @PrePersist
    protected void onCreate() {
        if (pinned == null) {
            pinned = false;
        }
    }
}
