package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public class Event {
        private Long timestamp;
        private Long userId;
        private EventType eventType;
        private OperationType operation;
        private Long eventId;
        private Long entityId;
    }
