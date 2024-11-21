package ru.yandex.practicum.filmorate.exception;

public class LikeNotExistException extends RuntimeException {
    public LikeNotExistException(String message) {
        super(message);
    }
}
