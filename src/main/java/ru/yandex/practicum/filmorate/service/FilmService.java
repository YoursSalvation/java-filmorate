package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.model.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingService ratingService;
    private final GenreService genreService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate MINIMAL_DATE = LocalDate.parse("1895-12-28", formatter);

    public Collection<FilmApiDto> getFilms() {
        return filmStorage.getFilms().stream()
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .toList();
    }

    public FilmApiDto getFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.getFilmById(id);
        FilmApiDto dto = FilmMapper.toDto(film);
        return dto;
    }

    public FilmApiDto deleteFilmById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = filmStorage.deleteFilmById(id);
        return FilmMapper.toDto(film);
    }

    public FilmApiDto createFilm(FilmApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("Film object shouldn't be null");
        Film film = FilmMapper.toFilm(dto);
        ratingService.checkFilmRating(film);
        genreService.checkFilmGenres(film);
        Film newFilm = filmStorage.createFilm(film);
        return FilmMapper.toDto(newFilm);
    }

    public FilmApiDto updateFilm(FilmApiDto dto) {
        if (dto == null) throw new IllegalArgumentException("Film object shouldn't be null");
        if (dto.getId() == null || dto.getId() < 1) throw new IllegalArgumentException("Invalid Film Id");
        Film film = FilmMapper.toFilm(dto);
        ratingService.checkFilmRating(film);
        genreService.checkFilmGenres(film);
        Film newFilm = filmStorage.updateFilm(film);
        return FilmMapper.toDto(newFilm);
    }

    public void addLike(Long filmId, Long userId) {
        if (filmId == null || filmId < 1) throw new NotFoundException("Invalid Film Id");
        if (userId == null || userId < 1) throw new NotFoundException("Invalid User Id");
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId == null || filmId < 1) throw new NotFoundException("Invalid Film Id");
        if (userId == null || userId < 1) throw new NotFoundException("Invalid User Id");
        filmStorage.removeLike(filmId, userId);
    }

    public List<FilmApiDto> getPopular(Integer count, Long genreId, String year) {
        return filmStorage.getPopular(count, genreId, year).stream()
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .toList();
    }
}