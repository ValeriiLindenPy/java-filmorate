package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @PositiveOrZero(message = "Timestamp должен быть положительным или равным нулю.")
    private Long timestamp;
    @NotNull(message = "UserId не может быть null.")
    private Long userId;
    @NotNull(message = "EventType не может быть null.")
    private EventType eventType;
    private OperationType operation;
    private Long eventId;
    private Long entityId;
}
