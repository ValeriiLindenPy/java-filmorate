package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @Null(groups = ValidationMarker.OnCreate.class)
    @NotNull(groups = ValidationMarker.OnUpdate.class, message = "Не указан id")
    private Long id;
    @Email(message = "Некорректный формат email.")
    @NotBlank(message = "Email не может быть null.")
    private String email;
    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы.")
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть null.")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
