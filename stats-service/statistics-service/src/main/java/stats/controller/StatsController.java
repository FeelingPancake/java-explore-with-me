package stats.controller;

import DTOlib.stats.MetricCreateDto;
import DTOlib.stats.MetricSummaryDto;
import DTOlib.util.LocalDateTimeCoder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stats.model.Metric;
import stats.service.StatsServiceImpl;
import stats.util.DtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private final StatsServiceImpl statsService;

    @PostMapping("/hit")
    public void get(@RequestBody @NotNull @Valid MetricCreateDto metricCreateDto) {
        Metric metric = DtoMapper.toMetric(metricCreateDto);

        statsService.saveStat(metric);
    }

    @GetMapping("/stats")
    public List<MetricSummaryDto> getStats(@RequestParam(value = "start") @NotBlank String start,
                                           @RequestParam(value = "end") @NotBlank String end,
                                           @RequestParam(value = "uris", required = false) String[] uris,
                                           @RequestParam(value = "unique", defaultValue = "false", required = false) Boolean unique) {
        LocalDateTime startDate = LocalDateTimeCoder.decodeDate(start);
        LocalDateTime endDate = LocalDateTimeCoder.decodeDate(end);
        log.debug("Декодированные строки - start : {}. end {}", start, end);

        return statsService.getStats(uris, startDate, endDate).stream()
                .map(metricSummary -> DtoMapper.toMetricSummaryDto(metricSummary, unique))
                .collect(Collectors.toList());
    }
}
