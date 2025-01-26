package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.MinimumDate;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
public class Film {

    private final Set<Long> likes = new HashSet<>();

    @NotNull(groups = {UpdateValidate.class})
    private Long id;

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @Size(max = 200,
            message = "Длина описания не должна превышать 200 символов")
    private String description;

    @MinimumDate
    private LocalDate releaseDate;

    @PositiveOrZero(message = "Продолжительность не может быть меньше нуля")
    private Integer duration;
}
