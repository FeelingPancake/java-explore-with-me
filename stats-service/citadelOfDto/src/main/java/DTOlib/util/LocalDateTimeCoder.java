package DTOlib.util;

import lombok.RequiredArgsConstructor;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RequiredArgsConstructor
public abstract class LocalDateTimeCoder {
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String encodeDate(LocalDateTime date) throws DateTimeException {
        String localDateTime = date.format(formatter);
        return URLEncoder.encode(localDateTime, StandardCharsets.UTF_8);
    }

    public static LocalDateTime decodeDate(String encodedDate) throws DateTimeParseException {
        String decodedString = URLDecoder.decode(encodedDate, StandardCharsets.UTF_8);
        return LocalDateTime.parse(decodedString, formatter);
    }
}
