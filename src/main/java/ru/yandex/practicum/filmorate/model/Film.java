package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.service.exception.ValidationMarker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    @Null(groups = ValidationMarker.OnCreate.class)
    @NotNull(groups = ValidationMarker.OnUpdate.class, message = "Не указан id")
    private Long id;

    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Имя не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Максимум для описания 200 символов.",
            groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Описание не может быть null.")
    private String description;

    @NotNull(groups = ValidationMarker.OnCreate.class, message = "Дата выпуска не может быть null.")
    private LocalDate releaseDate;

    @NotNull(groups = ValidationMarker.OnCreate.class, message = "Продолжительность не может быть null.")
    @Positive(message = "Продолжительность не может быть отрицательной.",
            groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private Integer duration;
    @Builder.Default
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();
}
