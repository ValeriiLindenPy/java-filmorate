package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.trace("check release date");
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("the date {} is before min - {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        log.trace("check film duration");
        if (film.getDuration() < 0) {
            log.warn("Film duration {} is less than 0", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        log.trace("set film id");
        film.setId(generateId());
        log.trace("add film to films");
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.trace("check new film if for null");
        if (newFilm.getId() == null) {
            log.warn("New film id is null");
            throw new ValidationException("Не указан id");
        }
        log.trace("check if new film id is in films");
        if (films.containsKey(newFilm.getId())) {
            log.debug("making instance of old film");
            Film oldFilm = films.get(newFilm.getId());
            log.debug("validate name of the film");
            if (oldFilm.getName() != newFilm.getName() && films.values().stream()
                    .anyMatch(film -> film.getName().equals(newFilm.getName()))) {
                log.warn("film with name {} is already in films", newFilm.getName());
                throw new ValidationException("Фильм с таким названием уже есть!");
            }
            log.trace("add film in films");
            films.put(newFilm.getId(), newFilm);
            return newFilm;
        }
        log.warn("film with id - {} is not found", newFilm.getId());
        throw new ValidationException("Фильм не найден!");
    }

    private Long generateId() {
        Long currentId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
