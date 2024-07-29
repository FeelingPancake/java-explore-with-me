package stats.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
