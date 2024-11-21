package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;


import java.util.List;
import java.util.Optional;

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
}
