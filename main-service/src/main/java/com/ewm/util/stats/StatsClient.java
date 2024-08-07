package com.ewm.util.stats;

import com.ewm.model.Event;
import dtostorage.stats.MetricCreateDto;
import dtostorage.stats.MetricSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import statshttpclient.StatsHttpClient;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsClient {
    private final StatsHttpClient statsHttpClient;

    public HashMap<Long, Long> getMassStats(List<Event> events) {
        log.debug("Получить статистику для events - {}", events);
        HashMap<Long, Long> metricSummaryDtoHashMap = new HashMap<>();
        if (!events.isEmpty()) {
            String[] uris = events.stream().map(event -> "/event/" + event.getId()).toArray(String[]::new);
            LocalDateTime created = events.stream().min(Comparator.comparing(Event::getCreatedOn)).get().getCreatedOn();
            List<MetricSummaryDto> metricSummaryDtos =
                statsHttpClient.getStats(created, LocalDateTime.now(), uris, true);

            log.debug("Получена статистика - {}", metricSummaryDtos);

            if (metricSummaryDtos != null && !metricSummaryDtos.isEmpty()) {
                for (MetricSummaryDto metricSummaryDto : metricSummaryDtos) {
                    Long key = Long.parseLong(metricSummaryDto.getUri().replace("/event/", ""));
                    metricSummaryDtoHashMap.put(key, metricSummaryDto.getHits());
                }
            }
        }
        log.debug("Сформирована хэш-мап статистики - {}", metricSummaryDtoHashMap);
        return metricSummaryDtoHashMap;
    }

    public void createHit(MetricCreateDto metricCreateDto) {
        statsHttpClient.createHit(metricCreateDto);
    }

}
