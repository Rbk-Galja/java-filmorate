package ru.yandex.practicum.filmorate.exception;

public class ValuesNotFoundException extends NullPointerException {
    public ValuesNotFoundException(String message) {
        super(message);
    }
}
