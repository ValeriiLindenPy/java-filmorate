package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;

import java.util.List;
import java.util.Optional;

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
}
