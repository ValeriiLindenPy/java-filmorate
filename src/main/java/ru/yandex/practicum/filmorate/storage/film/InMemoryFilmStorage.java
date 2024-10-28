package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    /**
     * Films Map
     */
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID - %d не найден.".formatted(id));
        }
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
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

    @Override
    public void clear() {
        films.clear();
    }
}
