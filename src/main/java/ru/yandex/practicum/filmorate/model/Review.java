package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 1000, message = "Content length must be between 1 and 1000 characters")
    private String content;      // Текст отзыва

    @NotNull(message = "isPositive cannot be null")
    private Boolean isPositive;  // Тип отзыва (положительный/негативный)

    @NotNull(message = "User ID cannot be null")
    private Long userId;         // ID пользователя

    @NotNull(message = "Film ID cannot be null")
    private Long filmId;         // ID фильма

    private Long useful = 0L;
}
