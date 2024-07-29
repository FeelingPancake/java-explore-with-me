package stats.error;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class ErrorResponse {
    String error;
    String message;

    public void log() {
        log.warn("Ошибка - {}", error);
    }
}
