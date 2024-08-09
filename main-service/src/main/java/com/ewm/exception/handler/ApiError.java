package com.ewm.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Value
@Slf4j
@AllArgsConstructor
@Builder
public class ApiError {
        List<String> errors;
        String message;
        String reason;
        String status;
        String timestamp;

    public void log() {
        log.warn("Ошибка - {}", message);
    }
}
