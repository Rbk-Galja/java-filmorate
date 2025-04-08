package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User newUser) {
        return userStorage.update(newUser);
    }

    public User addFriends(Long id, Long friendId) {
        User user = userStorage.getById(id);
        User userFriends = userStorage.getById(friendId);
        if (userFriends == null) {
            log.error("Запрос добавления для несуществующего id = {}", friendId);
            throw new IdNotFoundException("Запрашиваемый id = " + friendId + " не существет");
        }
        if (user == null) {
            log.error("Запрос добавления от несуществующего id = {}", id);
            throw new IdNotFoundException("Указанный id = " + id + " не существет");
        }
        FriendshipStatus status = user.getFriends().get(friendId);
        if (status == null) {
            userStorage.addFriendStatus(id, friendId, "OUTGOING");
            log.info("Статус заявки от пользователя id = {} пользователю id = {} изменён на OUTGOING", id, friendId);
            userStorage.addFriendStatus(friendId, id, "UNCONFIRMED");
            log.info("Статус входящей заявки от пользователя id = {} пользователю id = {} изменен на UNCONFIRMED",
                    id, friendId);
        } else {
            switch (status) {
                case UNCONFIRMED:
                    userStorage.updateStatus(id, friendId, "CONFIRMED", id);
                    log.info("Статус исходящей заявки от пользователя id = {} пользователю id = {} изменен на CONFIRMED",
                            id, friendId);
                    userStorage.updateStatus(friendId, id, "CONFIRMED", friendId);
                    log.info("Статус входящей заявки от пользователя id = {} пользователю id = {} изменен на CONFIRMED",
                            id, friendId);
                case OUTGOING:
                    log.error("Заявка в друзья от пользователя id = {} пользователю id = {} уже была отправлена",
                            id, friendId);
                case CONFIRMED:
                    log.error("Уже есть подтвержденная заявка в друзья от пользователя id = {} пользователю id = {} ",
                            id, friendId);
            }
        }
        return userFriends;
    }

    public List<User> getFriendsList(long id) {
        return userStorage.getFriendsList(id);
    }

    public List<User> deleteFriend(long id, long friendId) {
        User user = userStorage.getById(id);
        User friendUser = userStorage.getById(friendId);
        if (friendUser == null) {
            log.error("Запрос удаления для несуществующего id = {}", friendId);
            throw new IdNotFoundException("Запрашиваемый id = " + friendId + " не существет");
        }
        if (user == null) {
            log.error("Запрос удаления от несущестующего id = {}", id);
            throw new IdNotFoundException("Запрашиваемый id = " + id + " не существет");
        }
        userStorage.deleteFriendFromTable(id, friendId);
        log.info("Пользователь {} удален из друзей у пользователя {}", friendUser, user);

        return getFriendsList(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        log.info("Начинаем получение списка общих друзей для пользователей id = {} id = {}", id, otherId);
        List<Long> userId = userStorage.getHashSetFriends(id).keySet().stream().toList();
        List<Long> commonId = userStorage.getHashSetFriends(otherId).keySet().stream()
                .filter(userId::contains)
                .toList();
        List<User> commonFriends = new ArrayList<>();
        commonId.stream()
                .filter(user1 -> commonFriends.add(userStorage.getById(user1)))
                .toList();
        log.info("Возвращаем список общих друзей для пользователей id = {} id = {}", id, otherId);
        return commonFriends;
    }
}
