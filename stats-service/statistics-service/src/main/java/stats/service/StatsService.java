package stats.service;

import stats.model.Metric;
import stats.model.MetricSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveStat(Metric metric);
    List<MetricSummary> getStats(String[] uris, LocalDateTime start, LocalDateTime end);
}
