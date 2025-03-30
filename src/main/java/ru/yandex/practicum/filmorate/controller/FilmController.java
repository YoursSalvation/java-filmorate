package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate MINIMAL_DATE = LocalDate.parse("1895-12-28", formatter);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) throw new ValidationException("Название не может быть пустым");
        if (film.getDescription().length() > 200) throw new ValidationException("Макисмальная длина описания" +
                " 200 символов");
        if (film.getReleaseDate().isBefore(MINIMAL_DATE)) throw new ValidationException("Дата релиза - не раньше" +
                " 28 декабря 1895 года");
        if (film.getDuration().isNegative()) throw new ValidationException("Длительность не может быть отрицательной");
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) throw new ValidationException("Id должен быть указан");
        if (!films.containsKey(film.getId())) throw new ValidationException("Фильм с указанным id не найден");
        Film actualFilm = films.get(film.getId());
        if (film.getName() == null) film.setName(actualFilm.getName());
        if (film.getDescription() == null) film.setDescription(actualFilm.getDescription());
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MINIMAL_DATE))
            film.setReleaseDate(actualFilm.getReleaseDate());
        if (film.getDuration() == null) film.setDuration(actualFilm.getDuration());
        films.put(film.getId(), film);
        return film;
    }

    private Long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
    }
}