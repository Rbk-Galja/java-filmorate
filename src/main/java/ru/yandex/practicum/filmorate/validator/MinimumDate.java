package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * аннотация для валидации даты релиза не ранее 28.12.1985
 */

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidateReleaseDate.class)
@Past
public @interface MinimumDate {
    String message() default "Дата не должна быть ранее {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}
