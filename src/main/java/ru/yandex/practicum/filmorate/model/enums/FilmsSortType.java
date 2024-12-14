package ru.yandex.practicum.filmorate.model.enums;

public enum FilmsSortType {
    YEAR, LIKES;

    public static FilmsSortType from(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "year" -> YEAR;
            case "likes" -> LIKES;
            default -> null;
        };
    }
}