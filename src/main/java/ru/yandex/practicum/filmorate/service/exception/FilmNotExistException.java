package ru.yandex.practicum.filmorate.service.exception;

public class FilmNotExistException extends RuntimeException {
    public FilmNotExistException(String message) {
        super(message);
    }
}
