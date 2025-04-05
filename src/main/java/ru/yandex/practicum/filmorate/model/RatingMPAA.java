package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.LinkedHashSet;

@Builder
@Data
@EqualsAndHashCode(of = "id")
public class RatingMPAA {

    @NotBlank(message = "id не может быть пустым")
    private long id;

    private String name;

    @JsonIgnore
    LinkedHashSet<RatingMPAA> ratingMPAA;

}
