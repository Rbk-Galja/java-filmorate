package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * аннотация для валидации логина на наличие пробелов
 */

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidateWhiteSpace.class)
public @interface WhiteSpace {
    String message() default "Логин не должен содержать пробелы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
