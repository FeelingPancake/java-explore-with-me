package stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stats.model.Metric;
import stats.model.MetricSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Metric, Long> {
    @Query("SELECT new stats.model.MetricSummary(m.app, m.uri, COUNT(DISTINCT m.ip), COUNT(m.ip)) " +
            "FROM Metric m " +
            "WHERE m.uri IN :uris AND m.timestamp BETWEEN :start AND :end " +
            "GROUP BY m.app, m.uri")
    List<MetricSummary> findMetricSummary(@Param("uris") String[] uris,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);
}
