package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleIdNotFound(final IdNotFoundException e) {
        return new ErrorResponse("Ошибка данных", "Пользователь с указанным id не найден");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleObjectNotFound(final ValidationException e) {
        return new ErrorResponse("Ошибка данных", "Объект с указанным id не найден");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleIncorrectPost(final ParameterNotValidException e) {
        return new ErrorResponse("Ошибка данных", "Некорректное значение параметра " +
                e.getParameter() + ": " + e.getReason());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleServerError(final Throwable e) {
        log.info("500 {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), "Ошибка работы сервера");
    }
}
