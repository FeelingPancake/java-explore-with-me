package stats.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "stats")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "stats_app", nullable = false, updatable = false)
    String app;
    @Column(name = "stats_uri", nullable = false, updatable = false)
    String uri;
    @Column(name = "stats_ip", nullable = false, updatable = false)
    String ip;
    @Column(name = "stats_timestamp", nullable = false, updatable = false)
    LocalDateTime timestamp;
}
