package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.like.LikeService;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;


    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getByID(@PathVariable long id) {
        return filmService.getById(id);
    }


    @GetMapping("/popular")
    public List<Film> getTop(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTop(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "likes") String sortBy) {

        return filmService.getFilmsByDirectorSorted(directorId, sortBy);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Validated(ValidationMarker.OnCreate.class) @Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        likeService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        likeService.removeLike(id, userId);
    }


    @PutMapping
    public Film update(@Validated(ValidationMarker.OnUpdate.class) @Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        return filmService.search(query, by);
    }
}
