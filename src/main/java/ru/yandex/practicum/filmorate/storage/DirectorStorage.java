package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class DirectorStorage {
    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    public Optional<Director> getById(Long id) {
        try {
            String FIND_DIRECTOR_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
            Director director = jdbc.queryForObject(FIND_DIRECTOR_BY_ID_QUERY, mapper, id);
            return Optional.of(director);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Director> getAll() {
        String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
        return jdbc.query(FIND_ALL_DIRECTORS_QUERY, mapper);
    }

    public Director create(Director director) {
        String insertDirectorQuery = "INSERT INTO directors (id, name) VALUES (?, ?)";
        jdbc.update(insertDirectorQuery, director.getId(), director.getName());
        return director;
    }

    public Director update(Director newDirector) {
        String updateDirectorQuery = "UPDATE directors SET name = ? WHERE id = ?";
        jdbc.update(updateDirectorQuery,
                newDirector.getName(), newDirector.getId());
        return newDirector;
    }

    public void deleteById(Long id) {
        String deleteDirectorQuery = "DELETE FROM directors WHERE id = ?";
        jdbc.update(deleteDirectorQuery, id);
    }

    public Map<Long, Set<Director>> getAllFilmsDirectors() {
        String sql = "SELECT f.ID AS film_id, d.ID AS director_id, d.NAME AS director_name\n" +
                "FROM FILMS f\n" +
                "LEFT JOIN FILM_DIRECTORS fd ON fd.FILM_ID = f.ID \n" +
                "LEFT JOIN DIRECTORS d ON d.ID = fd.DIRECTOR_ID";
        Map<Long, Set<Director>> filmDirectors = new HashMap<>();

        jdbc.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getObject("director_id", Long.class);
            String director_name = rs.getString("director_name");

            filmDirectors.computeIfAbsent(filmId, k -> new HashSet<>());
            if (directorId != null) {
                filmDirectors.get(filmId).add(Director.builder()
                        .id(directorId).name(director_name).build());
            }
        });

        return filmDirectors;
    }

    public void saveDirectors(Film film) {
        Set<Director> filmDirectors = film.getDirectors();

        if (filmDirectors == null || filmDirectors.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO film_directors (film_id, director_id) VALUES ");
        List<Object> params = new ArrayList<>();
        StringJoiner valuesJoiner = new StringJoiner(", ");

        for (Director filmDirector : filmDirectors) {
            valuesJoiner.add("(?, ?)");
            params.add(film.getId());
            params.add(filmDirector.getId());
        }

        sql.append(valuesJoiner);
        jdbc.update(sql.toString(), params.toArray());
    }

    public void updateDirectors(Film film) {
        String deleteSql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbc.update(deleteSql, film.getId());
        saveDirectors(film);
    }

}
