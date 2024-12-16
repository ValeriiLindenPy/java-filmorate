package ru.yandex.practicum.filmorate.model.enums;

public enum SortType {
    YEAR("year"),
    LIKES("likes");

    private final String alias;

    SortType(String alias) {
        this.alias = alias;
    }

    public static SortType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SortType value cannot be null or empty");
        }

        String lowerValue = value.toLowerCase();
        for (SortType sortType : values()) {
            if (sortType.name().equalsIgnoreCase(lowerValue) || sortType.alias.equalsIgnoreCase(lowerValue)) {
                return sortType;
            }
        }

        throw new IllegalArgumentException("Invalid sortBy parameter: " + value);
    }
}

