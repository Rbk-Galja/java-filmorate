package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId;

    @Override
    public Collection<User> findAll() {
        log.debug("Возвращаем список всех пользователей {}:\n", users.values());
        return users.values();
    }

    @Override
    public User add(User user) {
        log.info("Создание пользователя User: {} началось", user);
        user.setId(getNextId());
        log.info("Пользователю присвоен id = {}", user.getId());
        user.setName(userName(user));
        users.put(user.getId(), user);
        log.info("Создание пользователя User: {} завершено", user);
        return user;
    }

    @Override
    public User update(User newUser) {
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

    @Override
    public User addFriends(Long id, Long friendId) {
        User user = users.get(id);
        User userFriends = users.get(friendId);
        if (userFriends == null) {
            log.error("Запрос добавления для несуществующего id = {}", friendId);
            throw new IdNotFoundException("Запрашиваемый id не существет");
        }
        user.getFriends().add(friendId);
        userFriends.getFriends().add(id);
        log.info("Пользователь {} добавлен в друзья пользователю {}", user, userFriends);
        return userFriends;
    }

    @Override
    public List<User> getFriendsList(long id) {
        List<User> userList = new ArrayList<>();
        users.get(id).getFriends().stream()
                .filter(idUs -> userList.add(users.get(idUs)))
                .toList();
        log.info("Возвращаем список друзей пользователя c id = {}: \n {}", id, userList);
        return userList;
    }

    @Override
    public List<User> deleteFriend(long id, long friendId) {
        User user = users.get(id);
        User friendUser = users.get(friendId);
        if (friendUser == null) {
            log.error("Запрос удаления для несуществующего id = {}", friendId);
            throw new IdNotFoundException("Запрашиваемый id не существет");
        }
        user.getFriends().remove(friendId);
        log.info("Пользователь {} удален из друзей у пользователя {}", friendUser, user);
        friendUser.getFriends().remove(id);
        log.info("Пользователь {} удален из друзей у пользователя {}", user, friendUser);
        return getFriendsList(id);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        if (users.get(otherId) == null) {
            log.error("Запрос для несуществующего id = {}", otherId);
            throw new IdNotFoundException("Запрашиваемый id не существет");
        }
        List<Long> userId = users.get(id).getFriends().stream().toList();
        List<Long> commonId = users.get(otherId).getFriends().stream()
                .filter(comId -> userId.contains(comId))
                .toList();
        List<User> commonFriends = new ArrayList<>();
        commonId.stream()
                .filter(user -> commonFriends.add(users.get(user)))
                .toList();
        log.info("Возвращаем список общих друзей пользователей id = {} id = {} : \n {}", id, otherId, commonFriends);
        return commonFriends;
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
