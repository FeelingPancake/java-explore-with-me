package stats.repository;

import stats.model.MetricSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface MetricRepositoryCustom {
    List<MetricSummary> findMetricSummaries(String[] uris, LocalDateTime start, LocalDateTime end, boolean unique);
}
