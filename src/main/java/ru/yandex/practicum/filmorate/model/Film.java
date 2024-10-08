package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Максимум для описания 200 символов.")
    private String description;
    private LocalDate releaseDate;
    private int duration;
}
