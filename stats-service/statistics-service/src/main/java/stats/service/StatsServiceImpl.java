package stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import stats.model.Metric;
import stats.model.MetricSummary;
import stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveStat(Metric metric) {
        log.info("Вызов метода сервиса статистики для сохранения метрики с uri = '{}'", metric.getUri());

        statsRepository.save(metric);
    }

    @Override
    public List<MetricSummary> getStats(String[] uris, LocalDateTime start, LocalDateTime end) {
        log.info("Вызов метода сервиса статистики для получения обобщенной метрики по списку uri = {}",
                Arrays.toString(uris));
        List<MetricSummary> metricSummaries = statsRepository.findMetricSummary(uris, start, end);

        log.debug("Полученный список - {}", metricSummaries);

        return metricSummaries;
    }
}
