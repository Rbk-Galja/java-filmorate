package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.MinimumDate;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
public class Film {
    @NotNull(groups = {UpdateValidate.class})
    Long id;

    @NotBlank(message = "Название не должно быть пустым")
    String name;

    @Size(max = 200,
            message = "Длина описания не должна превышать 200 символов")
    String description;

    @MinimumDate
    LocalDate releaseDate;

    @PositiveOrZero(message = "Продолжительность не может быть меньше нуля")
    Integer duration;
}
