package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.service.exception.ValidationMarker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Null(groups = ValidationMarker.OnCreate.class)
    @NotNull(groups = ValidationMarker.OnUpdate.class, message = "Не указан id")
    private Long id;
    @Email(message = "Некорректный формат email.",
            groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Email не может быть null.")
    private String email;
    @NotBlank(groups = ValidationMarker.OnCreate.class, message = "Логин не может быть пустым.")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелы.",
            groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private String login;
    private String name;
    @NotNull(groups = ValidationMarker.OnCreate.class, message = "Дата рождения не может быть null.")
    @PastOrPresent(message = "Дата рождения не может быть в будущем",
            groups = {ValidationMarker.OnCreate.class, ValidationMarker.OnUpdate.class})
    private LocalDate birthday;
    @Builder.Default
    private Set<Long> friends = new HashSet<>();
}
