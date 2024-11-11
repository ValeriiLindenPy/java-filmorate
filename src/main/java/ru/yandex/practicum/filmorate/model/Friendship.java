package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;

@Data
@Builder
public class Friendship {
    @NotNull(message = "Идентификатор первого пользователя не может быть null.")
    private Long userId;

    @NotNull(message = "Идентификатор второго пользователя не может быть null.")
    private Long friendId;

    @NotNull(message = "Статус дружбы не может быть null.")
    private FriendshipStatus status;
}
