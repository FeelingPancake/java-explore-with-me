package com.ewm.util.stats;

import com.ewm.model.Event;
import dtostorage.stats.MetricCreateDto;
import dtostorage.stats.MetricSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import statshttpclient.StatsHttpClient;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {
    private final StatsHttpClient statsHttpClient;

    public HashMap<Long, Long> getMassStats(List<Event> events) {
        HashMap<Long, Long> metricSummaryDtoHashMap = new HashMap<>();
        if (!events.isEmpty()) {
            String[] uris = events.stream().map(event -> "/event/" + event.getId()).toArray(String[]::new);
            LocalDateTime created = events.stream().min(Comparator.comparing(Event::getCreatedOn)).get().getCreatedOn();
            ResponseEntity<Object> response =
                statsHttpClient.getStats(created, LocalDateTime.now(), uris, false);
            List<MetricSummaryDto> metricSummaryDtos = (List<MetricSummaryDto>) response.getBody();

            if (metricSummaryDtos != null && !metricSummaryDtos.isEmpty()) {
                metricSummaryDtos.stream().map(metricSummaryDto -> {
                    Long key = Long.parseLong(metricSummaryDto.getUri().replace("/event/", ""));
                    metricSummaryDtoHashMap.put(key, metricSummaryDto.getHits());
                    return null;
                });
            }
        }
        return metricSummaryDtoHashMap;
    }

    public void createHit(MetricCreateDto metricCreateDto) {
        statsHttpClient.createHit(metricCreateDto);
    }

}
