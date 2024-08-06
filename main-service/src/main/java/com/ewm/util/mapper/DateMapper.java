package com.ewm.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public class DateMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Named("formatLocalDateTime")
    public String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    @Named("parseLocalDateTime")
    public LocalDateTime parseLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, formatter);
    }
}
