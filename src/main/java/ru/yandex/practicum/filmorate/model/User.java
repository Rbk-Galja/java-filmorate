package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;
import ru.yandex.practicum.filmorate.validator.WhiteSpace;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    private final Set<Long> friends = new HashSet<>();

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

}
