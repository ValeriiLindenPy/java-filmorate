package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mapper.MPARowMapper;


import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MPAStorage {
    private final JdbcTemplate jdbc;
    private final MPARowMapper mapper;

    public Optional<MPA> getById(Long id) {
        try {
            String findMPAByIDQuery = "SELECT * FROM mpa_ratings WHERE id = ?";
            MPA mpa = jdbc.queryForObject(findMPAByIDQuery, mapper, id);
            return Optional.of(mpa);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }


    public List<MPA> getAll() {
        String findAllMPAQuery = "SELECT * FROM mpa_ratings";
        return jdbc.query(findAllMPAQuery, mapper);
    }

    public void saveMPA(Film film) {
        if (film.getMpa() != null) {
            String insertFilmMpaQuery = "INSERT INTO film_mpa (film_id, mpa_id) VALUES (?, ?)";
            jdbc.update(insertFilmMpaQuery, film.getId(), film.getMpa().getId());
        }
    }

    public void updateMPA(Film film) {
        String updateFilmMpaQuery = "UPDATE film_mpa SET mpa_id = ? WHERE film_id = ?";
        jdbc.update(updateFilmMpaQuery, film.getMpa().getId(), film.getId());
    }

}
