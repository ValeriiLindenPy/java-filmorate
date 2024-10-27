package ru.yandex.practicum.filmorate.service.exception;

public class DoubleLikeException extends RuntimeException {
    public DoubleLikeException(String message) {
        super(message);
    }
}
