package ru.yandex.practicum.filmorate.service.exception;

public class LikeNotExistException extends RuntimeException {
    public LikeNotExistException(String message) {
        super(message);
    }
}
