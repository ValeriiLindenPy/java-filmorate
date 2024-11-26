package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    /**
     * get a film by id in storage
     */
    Optional<Film> getById(Long id);

    /**
     * get all films in storage
     */
    Collection<Film> getAll();

    /**
     * create a film in storage
     */
    Film create(Film film);

    /**
     * update a film in storage
     */
    Film update(Film newFilm);

    /**
     * delete a film by id from storage
     */
    void deleteById(Long id);

    Set<Long> getLikes(java.lang.Long filmId);

    Collection<Film> getTop(int count);

    void addLike(Long userId, Long filmId);

    void removeLike(Long userId, Long filmId);
}
