package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    private final FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film addFilm(@Valid Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(@Validated(UpdateValidate.class) Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film likeFilm(@Validated(UpdateValidate.class) long id,
                         @Validated(UpdateValidate.class) long userId) {
        return filmStorage.likeFilm(id, userId);
    }

    public Film deleteLike(@Validated(UpdateValidate.class) long id,
                           @Validated(UpdateValidate.class) long userId) {
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> countPopularFilm(int count) {
        return filmStorage.countPopularFilm(count);
    }
}
