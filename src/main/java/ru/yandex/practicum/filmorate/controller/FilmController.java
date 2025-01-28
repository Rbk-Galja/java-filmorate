package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/popular")
    public List<Film> countPopularFilm(@RequestParam(defaultValue = "10") int count) {
        if (count < 0) {
            throw new ParameterNotValidException("count", "Значение не может быть меньше нуля");
        }
        return filmService.countPopularFilm(count);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Validated(UpdateValidate.class) Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable @Validated(UpdateValidate.class) long id,
                         @PathVariable @Validated(UpdateValidate.class) long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable @Validated(UpdateValidate.class) long id,
                           @PathVariable @Validated(UpdateValidate.class) long userId) {
        return filmService.deleteLike(id, userId);
    }
}
