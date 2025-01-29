package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface UserStorage extends Storage<User> {
    User addFriends(Long id, Long friendId);

    List<User> getFriendsList(long id);

    List<User> deleteFriend(long id, long friendId);

    List<User> getCommonFriends(long id, long otherId);
}
