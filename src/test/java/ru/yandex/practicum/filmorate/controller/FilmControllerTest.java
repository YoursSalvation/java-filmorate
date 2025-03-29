package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    @Test
    public void postFilm() {
        FilmController filmController = new FilmController();
        assertThrows(ValidationException.class, () -> filmController.addFilm(new Film("  ", "123",
                LocalDateTime.now(), Duration.ofMinutes(120))));
        assertThrows(ValidationException.class, () -> filmController.addFilm(new Film("  ", "123",
                LocalDateTime.parse("20.11.1894, 00:00", formatter), Duration.ofMinutes(120))));
    }

    @Test
    public void updateFilm() {
        FilmController filmController = new FilmController();
        filmController.addFilm(new Film("name", "desc", LocalDateTime.now(), Duration.ofMinutes(30)));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(new Film("name",
                "desc", LocalDateTime.now(), Duration.ofMinutes(30))));
    }
}