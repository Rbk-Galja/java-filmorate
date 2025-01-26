package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final InMemoryUserStorage userStorage;
    private long nextId;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.debug("Возвращаем список всех фильмов {}", films.values());
        return films.values();
    }

    @Override
    public Film add(Film film) {
        log.info("Создание фильма Film: {} началось", film);
        film.setId(getNextId());
        log.info("Фильму присвоен id = {}", film.getId());
        films.put(film.getId(), film);
        log.info("Создание фильма Film: {} завершено", film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        log.info("Обновление фильма Film: {} началось", oldFilm);
        if (oldFilm != null) {
            films.put(oldFilm.getId(), newFilm);
            log.info("Обновление фильма Film: {} завершено", newFilm);
            return newFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film likeFilm(long id, long userId) {
        Film film = films.get(id);
        if (film == null) {
            log.error("Запрос добавления лайка для несуществующего id = {}", id);
            throw new IdNotFoundException("Фильм с указанным id = " + id + " не найден");
        }
        if (userStorage.getById(userId) == null) {
            log.error("Запрос добавления лайка от несуществующего пользователя id = {}", userId);
            throw new IdNotFoundException("Пользователь с указанным id = " + userId + " не найден");
        }
        film.getLikes().add(userId);
        log.info("Лайк для фильма {} добавлен", film);
        return film;
    }

    @Override
    public Film deleteLike(long id, long userId) {
        Film film = films.get(id);
        if (film == null) {
            log.error("Запрос на удаление лайка для несуществующего id = {}", id);
            throw new IdNotFoundException("Фильм с указанным id = " + id + " не найден");
        }
        if (userStorage.getById(userId) == null) {
            log.error("Запрос на удаление лайка от несуществующего пользователя id = {}", userId);
            throw new IdNotFoundException("Пользователь с указанным id = " + userId + " не найден");
        }
        film.getLikes().remove(userId);
        log.info("Лайк у фильма {} удален", film);
        return film;
    }

    @Override
    public List<Film> countPopularFilm(int count) {
        List<Film> allFilm = new ArrayList<>(films.values().stream().toList());
        List<Film> sortFilm = allFilm.stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
        log.info("Возвращаем запрошенный список фильмов с максимальным количеством лайков {}", sortFilm);
        return sortFilm;
    }

    private long getNextId() {
        return ++nextId;
    }
}
