package statshttpclient;

import dtostorage.stats.MetricCreateDto;
import dtostorage.util.LocalDateTimeCoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.Map;

public class StatsHttpClient extends BaseHttpClient {
    @Autowired
    public StatsHttpClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String serializedStart = LocalDateTimeCoder.encodeDate(start);
        String serializedEnd = LocalDateTimeCoder.encodeDate(end);
        Map<String, Object> parameters = Map.of(
            "start", serializedStart,
            "end", serializedEnd,
            "uris", uris,
            "unique", unique
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public ResponseEntity<Object> createHit(MetricCreateDto metricCreateDto) {
        return post("/hit", metricCreateDto);
    }
}
