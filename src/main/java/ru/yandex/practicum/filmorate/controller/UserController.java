package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private static long nextId;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Возвращаем список всех пользователей");
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.info("Создание пользователя User: {} началось", user);
        user.setId(getNextId());
        log.info("Пользователю присвоен id = {}", user.getId());
        user.setName(userName(user));
        users.put(user.getId(), user);
        log.info("Создание пользователя User: {} завершено", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Validated(UpdateValidate.class) User newUser) {
        User oldUser = users.get(newUser.getId());
        log.info("Обновление пользователя User: {} началось", oldUser);
        if (oldUser != null) {
            newUser.setName(userName(newUser));
            log.info("Поле имя обновлено на {}", newUser.getName());
            users.put(oldUser.getId(), newUser);
            log.info("Обновление пользователя User: {} завершено", newUser);
            return newUser;
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private String userName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin();
        }
        return user.getName();
    }

    private long getNextId() {
        return ++nextId;
    }
}
