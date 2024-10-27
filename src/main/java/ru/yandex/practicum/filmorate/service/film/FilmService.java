package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        log.trace("Check release date");
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("The date {} is before min - {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        log.trace("Set film id");
        film.setId(generateId());
        log.trace("Add film to films");
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.trace("check if new film id is in films");
        if (filmStorage.getById(newFilm.getId()) != null) {
            log.trace("add film in films");
            return filmStorage.update(newFilm);
        }
        log.warn("film with id - {} is not found", newFilm.getId());
        throw new FilmNotExistException("Фильм не найден!");
    }

    public Collection<Film> getTop(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


    public void addLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        User user = userStorage.getById(userId);

        if (film == null) {
            throw new FilmNotExistException("Фильм с ID - %d не найден.".formatted(id));
        } else if (user == null) {
            throw new UserNotExistException("Пользователь с ID - %d не найден.".formatted(userId));
        }

        if (film.getLikes().contains(user)) {
            throw new DoubleLikeException("Вы уже лайкали этот фильм!");
        }

        film.getLikes().add(user);
    }

    public void removeLike(long id, long userId) {
        Film film = filmStorage.getById(id);
        User user = userStorage.getById(userId);

        if (film == null) {
            throw new FilmNotExistException("Фильм с ID - %d не найден.".formatted(id));
        } else if (user == null) {
            throw new UserNotExistException("Пользователь с ID - %d не найден.".formatted(userId));
        }

        if (!film.getLikes().contains(user)) {
            throw new LikeNotExistException("Вы не лайкали этот фильм!");
        }

        film.getLikes().remove(user);
    }

    private Long generateId() {
        Long currentId = filmStorage.getAll().stream()
                .mapToLong(film -> film.getId())
                .max()
                .orElse(0);
        return ++currentId;
    }

}
