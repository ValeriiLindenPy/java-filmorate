package ru.yandex.practicum.filmorate.model.enums;

public enum FilmsSearchBy {
    TITLE, DIRECTOR, ALL;

    public static FilmsSearchBy from(String param) {
        return switch (param) {
            case "title" -> TITLE;
            case "director" -> DIRECTOR;
            case "title,director", "director,title" -> ALL;
            default -> null;
        };
    }
}
