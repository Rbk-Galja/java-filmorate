package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.MinimumDate;
import ru.yandex.practicum.filmorate.validator.UpdateValidate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
    private long rate;

    private RatingMPAA mpa;

    @JsonIgnore
    private Set<Long> likes = new HashSet<>();

    private LinkedHashSet<Category> genres;

    @Builder
    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, RatingMPAA mpa,
                LinkedHashSet<Category> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;

        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }
}
