package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/*
 * валидация даты релиза
 */

public class ValidateReleaseDate implements ConstraintValidator<MinimumDate, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(MinimumDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(minimumDate);
    }
}
