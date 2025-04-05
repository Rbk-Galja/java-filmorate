package ru.yandex.practicum.filmorate.exception;

import org.springframework.dao.EmptyResultDataAccessException;

public class EmptyResultSelectException extends EmptyResultDataAccessException {

    public EmptyResultSelectException(String msg, int expectedSize) {
        super(msg, expectedSize);
    }
}
