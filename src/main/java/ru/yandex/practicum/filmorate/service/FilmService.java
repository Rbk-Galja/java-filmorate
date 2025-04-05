package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
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
