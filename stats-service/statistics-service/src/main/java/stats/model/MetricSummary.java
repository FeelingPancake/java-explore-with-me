package stats.model;

public record MetricSummary(String app, String uri, Long uniqueHits, Long hits) {
};
