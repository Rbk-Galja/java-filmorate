package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.MinimumDate;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class FilmTests {

    private static final Validator validator;
    static FilmController filmController;
    Film film;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @BeforeEach
    public void createObject() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Властелин колец: Братство кольца ")
                .description("Сказания о Средиземье — это хроника Великой войны за Кольцо, длившейся не одну тысячу лет. " +
                        "Тот, кто владел Кольцом, получал неограниченную власть, но был обязан служить злу.")
                .releaseDate(LocalDate.of(2001, 12, 10))
                .duration(178)
                .build();
    }

    @Test
    @DisplayName("Создание валидного объекта Film")
    void createValidFilm() {
        Film expectedFilm = filmController.addFilm(film);
        assertEquals(film.getId(), expectedFilm.getId(), "Возвращает неверный объект");
        assertEquals(film, expectedFilm, "Не сохраняет объект в список");
    }

    @Test
    @DisplayName("Обновление валидного фильма")
    void updateValidFilm() {
        filmController.addFilm(film);
        Film updateFilms = Film.builder()
                .id(film.getId())
                .name("new name")
                .description("new description")
                .releaseDate(LocalDate.of(2021, 10, 10))
                .duration(100)
                .build();

        Film exspectedFilm = filmController.updateFilm(updateFilms);
        assertEquals(film.getName(), exspectedFilm.getName(), "Не обновляет фильм");
        assertEquals(film.getId(), exspectedFilm.getId(), "Неверный id обновленного фильма");
    }

    @Test
    @DisplayName("Валидация пустого Film name")
    void validateNameFilm() {
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("name", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация длины Film description не более 200 символов")
    void validateDescriptionFilm() {
        film.setDescription("Сказания о Средиземье — это хроника Великой войны за Кольцо, длившейся не одну тысячу лет. " +
                "Тот, кто владел Кольцом, получал неограниченную власть, но был обязан служить злу. " +
                "Тихая деревня, где живут хоббиты.");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("description", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация Film releaseDate не ранее 28.12.1985")
    void validateReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(MinimumDate.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("releaseDate", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Валидация Film duration - положительное число")
    void validateDuration() {
        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Исключения не найдены");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(PositiveOrZero.class, violation.getConstraintDescriptor().getAnnotation().annotationType(),
                "Неправильный тип ошибки");
        assertEquals("duration", violation.getPropertyPath().toString(), "Неверное имя поля");
    }

    @Test
    @DisplayName("Проверка исключения при обновлении с несуществующим id")
    void exceptionIfIdNotExist() {
        filmController.addFilm(film);
        Film filmUpdate = Film.builder()
                .id(2L)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2004, 2, 2))
                .duration(20)
                .build();
        assertThrows(ValidationException.class, () -> filmController.updateFilm(filmUpdate));
    }
}