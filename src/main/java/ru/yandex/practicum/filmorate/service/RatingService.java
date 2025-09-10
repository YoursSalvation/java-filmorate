package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.RatingApiDto;
import ru.yandex.practicum.filmorate.model.RatingMapper;
import ru.yandex.practicum.filmorate.storage.RatingDbStorage;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingDbStorage ratingDBStorage;

    public Collection<RatingApiDto> getRatings() {
        return ratingDBStorage.getRatings().stream()
                .filter(Objects::nonNull)
                .map(RatingMapper::toDto)
                .toList();
    }

    public RatingApiDto getRatingById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid Rating Id");
        Rating rating = ratingDBStorage.getRatingById(id);
        return RatingMapper.toDto(rating);
    }

    public void checkFilmRating(Film film) {
        if (film == null) throw new IllegalArgumentException("Invalid Null Film");
        Rating rating = film.getRating();
        if (rating == null) return;
        ratingDBStorage.getRatingById(rating.getId());
    }
}