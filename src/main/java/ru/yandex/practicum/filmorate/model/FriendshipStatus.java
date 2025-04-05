package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FriendshipStatus {
    UNCONFIRMED("UNCONFIRMED"),
    OUTGOING("OUTGOING"),
    CONFIRMED("CONFIRMED");

    private final String val;

    public String getVal() {
        return this.val;
    }

    public static FriendshipStatus getStatus(String val) {
        return FriendshipStatus.valueOf(val);
    }
}
