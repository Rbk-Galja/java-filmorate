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
    Map<Long, User> users = new HashMap<>();

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
        log.info("Обновление пользователя User: {} началось", newUser);
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setName(userName(newUser));
            log.info("Поле имя обновлено на {}", newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            log.info("Поле email обновлено на {}", newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            log.info("Поле логин обновлено на {}", newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Поле дата рождения обновлено на {}", newUser.getBirthday());
            log.info("Обновление пользователя User: {} завершено", newUser);
            return oldUser;
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
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
