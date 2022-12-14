package org.example.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class LocalDateConverter {

    public static Date convertToDatabaseColumn(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(Date::valueOf)
                .orElse(null);
    }

    public static LocalDate convertToEntityAttribute(Date date) {
        return Optional.ofNullable(date)
                .map(Date::toLocalDate)
                .orElse(null);
    }

    public static LocalDate convertToEntityAttribute(String date) {
        return Optional.ofNullable(date).map(Date::valueOf)
                .map(Date::toLocalDate)
                .orElse(null);
    }

    public static Date convertToDatabaseColumn(LocalDateTime localDate) {
        return Optional.ofNullable(localDate).map(s -> Date.valueOf(localDate.toLocalDate())).orElse(null);
    }

    public static LocalDateTime convertToEntityAttributeTime(Date date) {
        return LocalDateTime.of(date.toLocalDate(), LocalTime.ofSecondOfDay(date.getTime()));
    }
}
