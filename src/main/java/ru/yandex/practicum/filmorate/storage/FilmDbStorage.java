package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Collection<Film> getFilms() {
        return jdbc.query(FilmRowMapper.GET_FILMS_QUERY, new FilmRowMapper());
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            return jdbc.queryForObject(FilmRowMapper.GET_FILM_BY_ID_QUERY, new FilmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film not found");
        }
    }

    @Override
    public Film deleteFilmById(Long id) {
        Film film;
        try {
            film = jdbc.queryForObject(FilmRowMapper.GET_SIMPLE_FILM_QUERY, new FilmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film not found");
        }
        jdbc.update(FilmRowMapper.DELETE_FILM_BY_ID_QUERY, id);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(FilmRowMapper.CREATE_FILM_QUERY, new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());

                if (film.getReleaseDate() != null) {
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                } else {
                    ps.setNull(3, Types.DATE);
                }

                if (film.getDuration() != null) {
                    ps.setLong(4, film.getDuration().toMillis());
                } else {
                    ps.setNull(4, Types.BIGINT);
                }

                if (film.getMpa() != null && film.getMpa().getId() != null) {
                    ps.setLong(5, film.getMpa().getId());
                } else {
                    ps.setNull(5, Types.BIGINT);
                }

                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Film duplicate key error");
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Film referential integrity error");
        }
        film.setId(keyHolder.getKey().longValue());
        try {
            List<Genre> genres = film.getGenres().stream()
                    .filter(g -> g.getId() != null)
                    .toList();

            if (!genres.isEmpty()) {
                String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES " +
                        genres.stream()
                                .map(g -> "(?, ?)")
                                .collect(Collectors.joining(", "));

                List<Object> genreIds = new ArrayList<>();
                for (Genre g : genres) {
                    genreIds.add(film.getId());
                    genreIds.add(g.getId());
                }

                jdbc.update(sql, genreIds.toArray());
            }
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Genre Referential integrity error");
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmById(film.getId());
        Long updateDuration = (film.getDuration() == null) ? null : film.getDuration().toMillis();
        Long updateRating = (film.getMpa() == null) ? null : film.getMpa().getId();
        try {
            jdbc.update(FilmRowMapper.UPDATE_FILM_QUERY,
                    film.getName(), film.getDescription(), film.getReleaseDate(), updateDuration, updateRating,
                    film.getId());
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Film duplicate key error");
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Film referential integrity error");
        }
        try {
            jdbc.update(FilmRowMapper.REMOVE_FILM_GENRES_QUERY, film.getId());
            List<Genre> genres = film.getGenres().stream()
                    .filter(g -> g.getId() != null)
                    .toList();

            if (!genres.isEmpty()) {
                String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES " +
                        genres.stream()
                                .map(g -> "(?, ?)")
                                .collect(Collectors.joining(", "));

                List<Object> genreIds = new ArrayList<>();
                for (Genre g : genres) {
                    genreIds.add(film.getId());
                    genreIds.add(g.getId());
                }

                jdbc.update(sql, genreIds.toArray());
            }
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Film referential integrity error");
        }

        return getFilmById(film.getId());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        try {
            jdbc.update(FilmRowMapper.ADD_LIKE_QUERY, filmId, userId);
        } catch (DuplicateKeyException e) {
            // do nothing
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbc.update(FilmRowMapper.REMOVE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getPopular(Integer count, Long genreId, String year) {
        final String QUERY_GROUP = "GROUP BY f.film_id ORDER BY COUNT(DISTINCT l.user_id) DESC  LIMIT ?;";
        final String QUERY_GENRE_ID =
                "JOIN film_genres AS fgfilter ON f.film_id = fgfilter.film_id AND fgfilter.genre_id = ? ";
        final String QUERY_YEAR = "WHERE EXTRACT(YEAR FROM f.release_date) = ? ";
        if (Objects.nonNull(genreId) && Objects.isNull(year)) {
            return jdbc.query(
                    FilmRowMapper.GET_POPULAR_FILMS_QUERY
                            + QUERY_GENRE_ID
                            + QUERY_GROUP,
                    new FilmRowMapper(), genreId, count);
        }
        if (Objects.isNull(genreId) && Objects.nonNull(year)) {
            return jdbc.query(
                    FilmRowMapper.GET_POPULAR_FILMS_QUERY
                            + QUERY_YEAR
                            + QUERY_GROUP,
                    new FilmRowMapper(), year, count);
        }
        if (Objects.nonNull(genreId) && Objects.nonNull(year)) {
            return jdbc.query(
                    FilmRowMapper.GET_POPULAR_FILMS_QUERY
                            + QUERY_GENRE_ID
                            + QUERY_YEAR
                            + QUERY_GROUP,
                    new FilmRowMapper(), genreId, year, count);
        }

        return jdbc.query(FilmRowMapper.GET_POPULAR_FILMS_QUERY + QUERY_GROUP, new FilmRowMapper(), count);
    }

    @Override
    public void checkFilmById(Long id) {
        try {
            jdbc.queryForObject(FilmRowMapper.GET_SIMPLE_FILM_QUERY, new FilmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Check failed: Film not found");
        }
    }
}