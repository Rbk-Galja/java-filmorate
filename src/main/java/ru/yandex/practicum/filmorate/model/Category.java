package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Category {

    @NotBlank(message = "id не может быть пустым")
    private long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @JsonIgnore
    Set<Category> category;

}
