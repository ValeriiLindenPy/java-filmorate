package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmsSearchBy;

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
    List<Film> getAll();

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


    List<Film> getTop(int count);

    List<Film> getDirectorFilmSortedByLike(Long directorId);

    List<Film> getDirectorFilmSortedByYear(Long directorId);

    List<Film> searchByParam(String query, FilmsSearchBy param);
}
