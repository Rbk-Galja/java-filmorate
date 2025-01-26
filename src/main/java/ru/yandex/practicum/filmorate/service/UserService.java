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

    public User updateUser(@Validated(UpdateValidate.class) User newUser) {
        return userStorage.update(newUser);
    }

    public User addFriends(@Validated(UpdateValidate.class) Long id,
                           @Validated(UpdateValidate.class) Long friendId) {
        return userStorage.addFriends(id, friendId);
    }

    public List<User> getFriendsList(@Validated(UpdateValidate.class) long id) {
        return userStorage.getFriendsList(id);
    }

    public List<User> deleteFriend(@Validated(UpdateValidate.class) long id,
                                   @Validated(UpdateValidate.class) long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(@Validated(UpdateValidate.class) long id,
                                       @Validated({UpdateValidate.class}) long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }
}
