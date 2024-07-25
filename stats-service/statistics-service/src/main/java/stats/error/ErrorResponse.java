package stats.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ErrorResponse(String error, String message) {
    public void log() {
        log.warn("Ошибка - {}", error);
    }
}
