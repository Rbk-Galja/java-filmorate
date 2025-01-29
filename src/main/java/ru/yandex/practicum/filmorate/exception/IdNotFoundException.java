package ru.yandex.practicum.filmorate.exception;

public class IdNotFoundException extends NullPointerException {
    public IdNotFoundException(String message) {
        super(message);
    }
}
