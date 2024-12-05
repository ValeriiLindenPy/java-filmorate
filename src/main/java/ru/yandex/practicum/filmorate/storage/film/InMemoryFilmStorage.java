package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    /**
     * Films Map
     */
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID - %d не найден.".formatted(id));
        }
        return Optional.of(film);
    }

    @Override
    public List<Film> getAll() {
        return films.values().stream().toList();
    }

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        films.put(oldFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
    }


    public Set<Long> getLikes(Long filmId) {
        return getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID - %d не найден.".formatted(filmId)))
                .getLikes();
    }

    @Override
    public List<Film> getTop(int count) {
        return List.of();
    }

    @Override
    public List<Film> getDirectorFilmSortedByLike(Long directorId) {
        //TODO method for local storage sort by like
        return List.of();
    }

    @Override
    public List<Film> getDirectorFilmSortedByYear(Long directorId) {
        //TODO method for local storage sort by year
        return List.of();
    }


    public void addLike(Long userId, Long filmId) {
        getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID - %d не найден.".formatted(filmId)))
                .getLikes()
                .add(userId);
    }


    public void removeLike(Long userId, Long filmId) {
        getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID - %d не найден.".formatted(filmId)))
                .getLikes()
                .remove(userId);
    }

}
