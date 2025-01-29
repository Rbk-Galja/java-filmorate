package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

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

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film likeFilm(long id, long userId) {
        return filmStorage.likeFilm(id, userId);
    }

    public Film deleteLike(long id, long userId) {
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> countPopularFilm(int count) {
        return filmStorage.countPopularFilm(count);
    }
}
