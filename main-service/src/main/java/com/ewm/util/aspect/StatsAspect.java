package com.ewm.util.aspect;

import com.ewm.util.stats.StatsClient;
import dtostorage.main.event.EventFullDto;
import dtostorage.main.event.EventShortDto;
import dtostorage.stats.MetricCreateDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Aspect
@RequiredArgsConstructor
public class StatsAspect {
    private static final String appName = "ewm";
    private static final String prefix = "/event/";
    private final StatsClient statsHttpClient;
    private final HttpServletRequest request;

    @Pointcut("execution(* com.ewm.controller.publicController.EventPublicController.getEvents(..))")
    public void statsEvents() {
    }


    @Pointcut("execution(* com.ewm.controller.publicController.EventPublicController.getEvent(..))")
    public void statsEvent() {
    }


    @Around("statsEvents()")
    public Object sendStatsEvents(ProceedingJoinPoint joinPoint) throws Throwable {
        String ip = request.getRemoteAddr();
        LocalDateTime time = LocalDateTime.now();
        Object result = joinPoint.proceed();
        List<EventShortDto> events = (List<EventShortDto>) result;

        for (EventShortDto event : events) {
            MetricCreateDto metricCreateDto = MetricCreateDto.builder()
                .app(appName).ip(ip).uri(prefix + event.getId()).timestamp(time).build();
            statsHttpClient.createHit(metricCreateDto);
        }

        MetricCreateDto allEvents =
            MetricCreateDto.builder().app(appName).ip(ip).uri("/events").timestamp(time).build();
        statsHttpClient.createHit(allEvents);

        return result;
    }

    @Around("statsEvent()")
    public Object sendStatsEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        String ip = request.getRemoteAddr();
        LocalDateTime time = LocalDateTime.now();
        Object result = joinPoint.proceed();
        EventFullDto eventFullDto = (EventFullDto) result;

        MetricCreateDto metricCreateDto = MetricCreateDto.builder()
            .app(appName).ip(ip).uri(prefix + eventFullDto.getId()).timestamp(time).build();
        statsHttpClient.createHit(metricCreateDto);

        return result;
    }


}
