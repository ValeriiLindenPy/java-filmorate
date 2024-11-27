package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;


import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreStorage {
    private static final String FIND_ALL_GENRE_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public Optional<Genre> getById(Long id) {
        try {
            Genre genre = jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, mapper, id);
            return Optional.of(genre);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }


    public List<Genre> getAll() {
        return jdbc.query(FIND_ALL_GENRE_QUERY, mapper);
    }

    public void saveGenres(Film film) {

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO film_genres (film_id, genre_id) VALUES ");
        List<Object> params = new ArrayList<>();
        StringJoiner valuesJoiner = new StringJoiner(", ");

        for (Genre genre : film.getGenres()) {
            valuesJoiner.add("(?, ?)");
            params.add(film.getId());
            params.add(genre.getId());
        }

        sql.append(valuesJoiner);
        jdbc.update(sql.toString(), params.toArray());
    }

    public void updateGenres(Film film) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(deleteSql, film.getId());
        saveGenres(film);
    }


}
