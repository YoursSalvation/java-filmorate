package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmApiDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmApiDto> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public FilmApiDto getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public FilmApiDto addFilm(@RequestBody FilmApiDto film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmApiDto updateFilm(@RequestBody FilmApiDto film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public FilmApiDto deleteFilmById(@PathVariable Long id) {
        return filmService.deleteFilmById(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmApiDto> getPopular(@RequestParam(defaultValue = "10") Integer count,
                                       @RequestParam(required = false) Long genreId,
                                       @RequestParam(required = false) String year) {
        return filmService.getPopular(count, genreId, year);
    }
}