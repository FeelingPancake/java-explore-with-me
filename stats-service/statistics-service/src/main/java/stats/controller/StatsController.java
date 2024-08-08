package stats.controller;

import dtostorage.stats.MetricCreateDto;
import dtostorage.util.LocalDateTimeCoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import stats.model.Metric;
import stats.model.MetricSummary;
import stats.service.StatsServiceImpl;
import stats.util.DtoMapper;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private final StatsServiceImpl statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void get(@RequestBody @NotNull @Valid MetricCreateDto metricCreateDto) {
        Metric metric = DtoMapper.toMetric(metricCreateDto);

        statsService.saveStat(metric);
    }

    @GetMapping("/stats")
    public List<MetricSummary> getStats(@RequestParam(value = "start") @NotBlank String start,
                                        @RequestParam(value = "end") @NotBlank String end,
                                        @RequestParam(value = "uris", required = false) String[] uris,
                                        @RequestParam(value = "unique", defaultValue = "false")
                                        Boolean unique) {
        LocalDateTime startDate = LocalDateTimeCoder.decodeDate(start);
        LocalDateTime endDate = LocalDateTimeCoder.decodeDate(end);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate должно быть больше endDate");
        }

        log.debug("Декодированные строки - start : {}. end {}", start, end);

        return statsService.getStats(uris, startDate, endDate, unique);
    }
}
