package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, UserDbStorage.class,
        FilmService.class, FilmDbStorage.class,
        RatingService.class, RatingDbStorage.class,
        GenreService.class, GenreDbStorage.class})
class FilmServiceTest {
    private final UserService userService;
    private final FilmService filmService;

    @BeforeEach
    void setUp() {
        UserApiDto u1 = new UserApiDto();
        u1.setName("Dima");
        u1.setLogin("dima");
        u1.setEmail("dima@ya.ru");
        userService.createUser(u1);

        UserApiDto u2 = new UserApiDto();
        u2.setName("Denis");
        u2.setLogin("denis");
        u2.setEmail("denis@ya.ru");
        userService.createUser(u2);

        FilmApiDto f1 = new FilmApiDto();
        f1.setName("Avengers");
        f1.setDescription("loki come back");
        filmService.createFilm(f1);

        FilmApiDto f2 = new FilmApiDto();
        f2.setName("Saw");
        f2.setDescription("the mystery of salvation");
        filmService.createFilm(f2);

        FilmApiDto f3 = new FilmApiDto();
        f3.setName("Jaws");
        f3.setDescription("shark is nearby");
        filmService.createFilm(f3);
    }

    @Test
    void likes() {
        filmService.addLike(1L, 1L);
        filmService.addLike(1L, 2L);
        filmService.addLike(2L, 1L);

        assertEquals(2, filmService.getFilmById(1L).getLikes().size());
        assertEquals(1, filmService.getFilmById(2L).getLikes().size());
        assertEquals(0, filmService.getFilmById(3L).getLikes().size());

        filmService.removeLike(1L, 1L);
        filmService.removeLike(1L, 2L);
        filmService.removeLike(2L, 1L);

        assertEquals(0, filmService.getFilmById(1L).getLikes().size());
        assertEquals(0, filmService.getFilmById(2L).getLikes().size());
        assertEquals(0, filmService.getFilmById(3L).getLikes().size());
    }

    @Test
    void getAndPopular() {
        Collection<FilmApiDto> films = filmService.getFilms();
        List<String> names = films.stream().filter(Objects::nonNull).map(FilmApiDto::getName).toList();
        assertEquals(3, names.size());
        assertEquals("Avengers", names.get(0));
        assertEquals("Saw", names.get(1));
        assertEquals("Jaws", names.get(2));

        filmService.addLike(3L, 1L);
        filmService.addLike(3L, 2L);
        filmService.addLike(2L, 1L);

        Collection<FilmApiDto> popular = filmService.getPopular(1000, null, null);
        List<String> popularNames = popular.stream().filter(Objects::nonNull).map(FilmApiDto::getName).toList();
        assertEquals(3, popularNames.size());
        assertEquals("Jaws", popularNames.get(0));
        assertEquals("Saw", popularNames.get(1));
        assertEquals("Avengers", popularNames.get(2));

        popular = filmService.getPopular(2, null, null);
        assertEquals(2, popular.size());

        assertThrows(IllegalArgumentException.class, () -> {
            filmService.getPopular(-1, null, null);
        });
    }

    @Test
    void crud() {
        FilmApiDto film = new FilmApiDto();
        film.setName("Time");
        film.setDescription("time is running out");
        film.setDuration(Duration.of(2, ChronoUnit.HOURS));
        film.setReleaseDate(LocalDate.of(2011, 10, 27));
        Rating rating = new Rating();
        rating.setId(1L);
        film.setRating(rating);
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        film.setGenres(genres);
        FilmApiDto createdFilm = filmService.createFilm(film);

        Long id = createdFilm.getId();

        createdFilm = filmService.getFilmById(id);
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getRating().getId(), createdFilm.getRating().getId());
        assertEquals(film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()),
                createdFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        FilmApiDto nextFilm = new FilmApiDto();
        nextFilm.setId(id);
        nextFilm.setName("Spiderman");
        nextFilm.setDescription("friendly neighbor");
        nextFilm.setDuration(Duration.of(2, ChronoUnit.HOURS));
        nextFilm.setReleaseDate(LocalDate.of(2002, 5, 3));
        Rating mpa2 = new Rating();
        mpa2.setId(2L);
        nextFilm.setRating(mpa2);
        Genre genre3 = new Genre();
        genre3.setId(3L);
        Genre genre4 = new Genre();
        genre4.setId(4L);
        Set<Genre> genres2 = new HashSet<>();
        genres2.add(genre3);
        genres2.add(genre4);
        nextFilm.setGenres(genres2);

        filmService.updateFilm(nextFilm);
        FilmApiDto updatedFilm = filmService.getFilmById(id);
        assertEquals(nextFilm.getId(), updatedFilm.getId());
        assertEquals(nextFilm.getName(), updatedFilm.getName());
        assertEquals(nextFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(nextFilm.getDuration(), updatedFilm.getDuration());
        assertEquals(nextFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(nextFilm.getRating().getId(), updatedFilm.getRating().getId());
        assertEquals(nextFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()),
                updatedFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));

        filmService.deleteFilmById(id);
        assertThrows(NotFoundException.class, () -> {
            filmService.deleteFilmById(id);
        });
    }
}