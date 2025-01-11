package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Возвращаем список всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Создание фильма Film: {} началось", film);
        film.setId(getNextId());
        log.info("Фильму присвоен id = {}", film.getId());
        films.put(film.getId(), film);
        log.info("Создание фильма Film: {} завершено", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Validated(UpdateValidate.class) Film newFilm) {
        log.info("Обновление фильма Film: {} началось", newFilm);
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            log.info("Поле имя обновлено на {}", newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            log.info("Поле описание обновлено на {}", newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Дата выхода обновлена на {}", newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Продолжительность изменена, новое значение {}", newFilm.getDuration());
            log.info("Обновление фильма Film: {} завершено", newFilm);
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
