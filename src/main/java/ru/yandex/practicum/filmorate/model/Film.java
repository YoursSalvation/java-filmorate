package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private Duration duration;

    //Конструктор для тестов
    public Film(String name, String description, LocalDateTime releaseDate, Duration duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}