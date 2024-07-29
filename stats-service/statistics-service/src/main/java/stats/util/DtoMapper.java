package stats.util;

import dtostorage.stats.MetricCreateDto;
import stats.model.Metric;

public abstract class DtoMapper {
    public static Metric toMetric(MetricCreateDto metricCreateDto) {
        return Metric.builder()
            .app(metricCreateDto.getApp())
            .uri(metricCreateDto.getUri())
            .timestamp(metricCreateDto.getTimestamp())
            .ip(metricCreateDto.getIp())
            .build();
    }
}
