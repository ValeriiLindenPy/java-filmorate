package ru.yandex.practicum.filmorate.model.enums;

public enum SortType {
    YEAR, LIKES;

    public static SortType fromString(String value) {
        try {
            return SortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sortBy parameter: " + value);
        }
    }
}

