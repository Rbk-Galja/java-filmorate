package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;
import ru.yandex.practicum.filmorate.validator.WhiteSpace;

import java.time.LocalDate;
import java.util.LinkedHashMap;

@Data
public class User {

    @NotNull(groups = {UpdateValidate.class})
    private Long id;

    @NotBlank(message = "Поле email пустое")
    @Email(message = "Указан некорректный формат email")
    private String email;

    @NotBlank
    @WhiteSpace
    private String login;

    private String name;

    @Past(message = "Указана некорректная дата рождения")
    private LocalDate birthday;

    @JsonIgnore
    private LinkedHashMap<Long, FriendshipStatus> friends = new LinkedHashMap<>();

    @Builder
    public User(Long id, String email, String login, String name, LocalDate birthday,
                LinkedHashMap<Long, FriendshipStatus> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public String userName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin();
        }
        return user.getName();
    }

}
