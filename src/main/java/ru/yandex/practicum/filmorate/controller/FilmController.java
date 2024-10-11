package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;
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
    @Validated(ValidationMarker.OnCreate.class)
    public Film create(@Valid @RequestBody Film film) {

        log.trace("Check release date");
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("The date {} is before min - {}", film.getReleaseDate(), MIN_DATE);
            throw new ValidationException("дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        log.trace("Set film id");
        film.setId(generateId());
        log.trace("Add film to films");
        films.put(film.getId(), film);
        return film;
    }


    @PutMapping
    @Validated(ValidationMarker.OnUpdate.class)
    public Film update(@Valid @RequestBody Film newFilm) {
        log.trace("check if new film id is in films");
        if (films.containsKey(newFilm.getId())) {
            log.debug("making instance of old film");
            Film oldFilm = films.get(newFilm.getId());
            log.trace("add film in films");
            films.put(oldFilm.getId(), newFilm);
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
