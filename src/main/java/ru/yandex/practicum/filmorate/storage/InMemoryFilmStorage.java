package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate MINIMAL_DATE = LocalDate.parse("1895-12-28", formatter);
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = films.get(id);
        if (film == null) throw new NotFoundException("Фильм не найден");
        return film;
    }

    @Override
    public Film deleteFilmById(Long id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank())
            throw new ValidationException("Название не может быть пустым");
        if (film.getDescription().length() > 200) throw new ValidationException("Макисмальная длина описания" +
                " 200 символов");
        if (film.getReleaseDate().isBefore(MINIMAL_DATE)) throw new ValidationException("Дата релиза - не раньше" +
                " 28 декабря 1895 года");
        if (film.getDuration().isNegative()) throw new ValidationException("Длительность не может быть отрицательной");
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        Set<Long> filmLikes = film.getLikes();
        filmLikes.add(userId);
        film.setLikes(filmLikes);
        films.put(filmId, film);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        Set<Long> filmLikes = film.getLikes();
        filmLikes.remove(userId);
        film.setLikes(filmLikes);
        films.put(filmId, film);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .toList();
    }

    private Long getNextId() {
        return films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(1) + 1;
    }
}