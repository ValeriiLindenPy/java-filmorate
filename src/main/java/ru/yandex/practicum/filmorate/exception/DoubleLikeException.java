package ru.yandex.practicum.filmorate.exception;

public class DoubleLikeException extends RuntimeException {
    public DoubleLikeException(String message) {
        super(message);
    }
}
