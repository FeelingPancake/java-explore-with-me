package statshttpclient;

import dtostorage.stats.MetricCreateDto;
import dtostorage.stats.MetricSummaryDto;
import dtostorage.util.LocalDateTimeCoder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class StatsHttpClient extends BaseHttpClient {
    @Autowired
    public StatsHttpClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()))
                .build()
        );
    }

    public List<MetricSummaryDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String serializedStart = LocalDateTimeCoder.encodeDate(start);
        String serializedEnd = LocalDateTimeCoder.encodeDate(end);
        Map<String, Object> parameters = Map.of(
            "start", serializedStart,
            "end", serializedEnd,
            "uris", uris,
            "unique", unique
        );
        ResponseEntity<List<MetricSummaryDto>> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters, new ParameterizedTypeReference<List<MetricSummaryDto>>() {});
        return response.getBody();
    }

    public ResponseEntity<Object> createHit(MetricCreateDto metricCreateDto) {
        return post("/hit", metricCreateDto);
    }
}
