package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Film {

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

    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    @JsonIgnore
    private long rate;

    @Builder
    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(long userId) {
        likes.add(userId);
        rate = likes.size();
    }

    public void removeLike(long userId) {
        likes.remove(userId);
        rate = likes.size();
    }

}
