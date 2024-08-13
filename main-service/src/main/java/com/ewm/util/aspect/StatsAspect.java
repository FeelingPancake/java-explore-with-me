package com.ewm.util.aspect;

import com.ewm.util.stats.StatsClient;
import dtostorage.main.event.EventFullDto;
import dtostorage.stats.MetricCreateDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Aspect
@RequiredArgsConstructor
public class StatsAspect {
    private static final String appName = "ewm-main-service";
    private static final String prefix = "/events/";
    private final StatsClient statsHttpClient;
    private final HttpServletRequest request;

    @Pointcut("execution(* com.ewm.controller.public_controller.EventPublicController.getEvents(..))")
    public void statsEvents() {
    }


    @Pointcut("execution(* com.ewm.controller.public_controller.EventPublicController.getEventForPublic(..))")
    public void statsEvent() {
    }


    @Around("statsEvents()")
    public Object sendStatsEvents(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("StatsAspect: sendStatsEvents called");
        String ip = request.getRemoteAddr();
        LocalDateTime time = LocalDateTime.now();
        Object result = joinPoint.proceed();
        List<EventFullDto> events = (List<EventFullDto>) result;

        for (EventFullDto event : events) {
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
        System.out.println("StatsAspect: sendStatsEvent called");
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
