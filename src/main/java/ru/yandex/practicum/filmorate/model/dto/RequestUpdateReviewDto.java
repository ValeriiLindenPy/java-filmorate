package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateReviewDto {
    @NotNull
    private Long reviewId;

    @Size(max = 1000, message = "Content length must be under 1000 characters")
    @NotBlank
    private String content;      // Текст отзыва

    private Boolean isPositive;  // Тип отзыва (положительный/негативный)
    private Long userId;         // ID пользователя
    private Long filmId;         // ID фильма
}
