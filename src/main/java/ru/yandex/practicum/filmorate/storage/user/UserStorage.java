package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.LinkedHashMap;
import java.util.List;

public interface UserStorage extends Storage<User> {

    List<User> getFriendsList(long id);

    void addFriendStatus(long id, long friendId, String status);

    void updateStatus(long userId, long friendId, String status, long sqlId);

    boolean deleteFriendFromTable(long id, long friendId);

    LinkedHashMap<Long, FriendshipStatus> getHashSetFriends(long id);
}
