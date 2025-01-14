package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/*
 * валидация пробелов в логине
 */

public class ValidateWhiteSpace implements ConstraintValidator<WhiteSpace, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
