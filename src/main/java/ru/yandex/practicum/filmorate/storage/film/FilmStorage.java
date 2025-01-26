package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    Film likeFilm(long id, long userId);

    Film deleteLike(long id, long userId);

    List<Film> countPopularFilm(int count);
}
