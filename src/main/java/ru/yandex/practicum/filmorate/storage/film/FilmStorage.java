package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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


    Collection<Film> getTop(int count);

    /**
     * Get common films between the user and their friend, sorted by popularity
     */
    List<Film> getCommonFilms(long userId, long friendId);
}
