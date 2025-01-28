package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserStorage userStorage;

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
        return userStorage.addFriends(id, friendId);
    }

    public List<User> getFriendsList(long id) {
        return userStorage.getFriendsList(id);
    }

    public List<User> deleteFriend(long id, long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }
}
