package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mapper.MPARowMapper;


import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MPAStorage {
    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM mpa_ratings";
    private static final String FIND_MPA_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final MPARowMapper mapper;

    public Optional<MPA> getById(Long id) {
        try {
            MPA mpa = jdbc.queryForObject(FIND_MPA_BY_ID_QUERY, mapper, id);
            return Optional.of(mpa);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }


    public List<MPA> getAll() {
        return jdbc.query(FIND_ALL_MPA_QUERY, mapper);
    }
}
