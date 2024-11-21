package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

//    @GetMapping("/popular")
//    public Collection<Film> getTop(@RequestParam(defaultValue = "10") int count) {
//        return filmService.getTop(count);
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Validated(ValidationMarker.OnCreate.class) @Valid @RequestBody Film film) {
        return filmService.create(film);
    }

//    @PutMapping("/{id}/like/{userId}")
//    public void addLike(@PathVariable long id, @PathVariable long userId) {
//        filmService.addLike(id, userId);
//    }
//
//    @DeleteMapping("/{id}/like/{userId}")
//    public void removeLike(@PathVariable long id, @PathVariable long userId) {
//        filmService.removeLike(id, userId);
//    }


    @PutMapping
    public Film update(@Validated(ValidationMarker.OnUpdate.class) @Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }
}
