package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmsSearchBy;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final FilmRowMapper mapper;
    private final JdbcTemplate jdbc;

    @Override
    public Optional<Film> getById(Long id) {
        try {
            String findFilmByIdQuery = "SELECT f.*, mr.name AS mpa_name " +
                    "FROM FILMS f " +
                    "LEFT JOIN MPA_RATINGS mr ON f.MPA_ID = mr.ID " +
                    "WHERE f.ID = ?";
            Film film = jdbc.queryForObject(findFilmByIdQuery, mapper, id);
            return Optional.of(film);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }


    @Override
    public Collection<Film> getAll() {
        String findAllFilmsQuery = "SELECT f.*, mr.ID AS mpa_id, mr.name AS mpa_name\n" +
                "FROM FILMS f\n" +
                "LEFT JOIN FILM_MPA fm ON f.ID = fm.FILM_ID  \n" +
                "LEFT JOIN MPA_RATINGS mr ON fm.MPA_ID = mr.ID";
        return jdbc.query(findAllFilmsQuery, mapper);
    }

    @Override
    public Film create(Film film) {
        String insertFilmQuery = "INSERT INTO films (id, name, description, duration, release_date, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbc.update(insertFilmQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        String updateFilmQuery = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id = ? WHERE id = ?";
        jdbc.update(updateFilmQuery,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getDuration(),
                newFilm.getReleaseDate(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public void deleteById(Long id) {
        String deleteFilmQuery = "DELETE FROM films WHERE id = ?";
        jdbc.update(deleteFilmQuery, id);
    }


    @Override
    public Collection<Film> getTop(int count) {
        String getLikesPopularQuery = "SELECT f.*, mr.ID AS mpa_id, mr.name AS mpa_name\n" +
                "FROM FILMS f \n" +
                "JOIN  (SELECT fl.film_id, COUNT(fl.user_id) AS likes \n" +
                "FROM FILM_LIKES fl\n" +
                "GROUP BY fl.film_id) likes_count ON f.ID = likes_count.film_id\n" +
                "LEFT JOIN FILM_MPA fm ON f.ID = fm.FILM_ID\n" +
                "LEFT JOIN MPA_RATINGS mr ON fm.MPA_ID = mr.ID\n" +
                "ORDER BY likes_count.likes DESC\n" +
                "LIMIT ?";
        return jdbc.query(getLikesPopularQuery, mapper, count);
    }

    @Override
    public List<Film> searchByParam(String query, FilmsSearchBy param) {
        String partOrder = " ORDER BY cnt DESC";
        String partWhereClause = "";
        switch (param) {
            case TITLE -> partWhereClause = " WHERE f.NAME ILIKE CONCAT('%',?,'%') ";
            case DIRECTOR -> partWhereClause = " WHERE d.NAME ILIKE CONCAT('%',?,'%') ";
            case ALL -> partWhereClause = " WHERE f.NAME ILIKE CONCAT('%',?,'%') OR d.NAME ILIKE CONCAT('%',?,'%')";
        }
        String findFilmsByParamQuery = """
                SELECT f.ID
                , f.NAME
                , f.DESCRIPTION
                , f.DURATION
                , f.RELEASE_DATE
                , mr.ID AS MPA_ID
                , mr.NAME AS MPA_NAME
                , COUNT(fl.USER_ID) OVER (PARTITION BY f.ID) cnt
                FROM FILMS f
                LEFT JOIN FILM_MPA fm ON f.ID = fm.FILM_ID
                LEFT JOIN MPA_RATINGS mr ON fm.MPA_ID = mr.ID
                LEFT JOIN FILM_LIKES fl ON fl.FILM_ID = f.ID
                LEFT JOIN FILM_DIRECTORS fd ON fd.FILM_ID = f.ID
                LEFT JOIN DIRECTORS d ON d.ID = fd.DIRECTOR_ID
                """ + partWhereClause + partOrder;
        if (param == FilmsSearchBy.ALL) {
            return jdbc.query(findFilmsByParamQuery, mapper, query, query);
        }
        return jdbc.query(findFilmsByParamQuery, mapper, query);
    }
}
