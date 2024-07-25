package stats.util;

import DTOlib.stats.MetricCreateDto;
import DTOlib.stats.MetricSummaryDto;
import stats.model.Metric;
import stats.model.MetricSummary;

public abstract class DtoMapper {
    public static Metric toMetric(MetricCreateDto metricCreateDto) {
        return Metric.builder()
                .app(metricCreateDto.getApp())
                .uri(metricCreateDto.getUri())
                .timestamp(metricCreateDto.getTimestamp())
                .ip(metricCreateDto.getIp())
                .build();
    }

    public static MetricSummaryDto toMetricSummaryDto(MetricSummary metricSummary, Boolean unique) {
        return MetricSummaryDto.builder()
                .app(metricSummary.app())
                .uri(metricSummary.uri())
                .hits(unique ? metricSummary.uniqueHits() : metricSummary.hits()).build();
    }
}
