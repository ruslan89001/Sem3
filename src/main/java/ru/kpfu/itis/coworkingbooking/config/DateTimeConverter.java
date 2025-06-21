package ru.kpfu.itis.coworkingbooking.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DateTimeConverter implements Converter<String, LocalDateTime> {
    
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    };
    
    @Override
    public LocalDateTime convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }
        
        String trimmedSource = source.trim();
        
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedSource, formatter);
            } catch (DateTimeParseException e) {
                // Продолжаем пробовать следующий формат
            }
        }
        
        throw new IllegalArgumentException("Не удалось преобразовать строку в дату: " + source);
    }
} 