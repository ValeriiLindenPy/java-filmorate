package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;
import java.util.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public Optional<Genre> getById(Long id) {
        try {
            String findGenreByIDQuery = "SELECT * FROM genres WHERE id = ?";
            Genre genre = jdbc.queryForObject(findGenreByIDQuery, mapper, id);
            return Optional.of(genre);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }


    public List<Genre> getAll() {
        String findAllGenreQuery = "SELECT * FROM genres";
        return jdbc.query(findAllGenreQuery, mapper);
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

    public Set<Genre> getFilmGenres(Long filmId) {
        String findGenresByFilmId = "SELECT g.* \n" +
                "FROM GENRES g \n" +
                "WHERE g.ID IN (\n" +
                "SELECT fg.genre_id\n" +
                "FROM FILM_GENRES fg \n" +
                "WHERE fg.film_id = ?\n" +
                ")";
        return new HashSet<>(jdbc.query(findGenresByFilmId, mapper, filmId));
    }

    public Map<Long, Set<Genre>> getAllFilmsGenres() {
        String sql = "SELECT f.ID AS film_id, g.ID AS genre_id, g.NAME AS genre_name " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_GENRES fg ON fg.FILM_ID = f.ID " +
                "LEFT JOIN GENRES g ON g.ID = fg.GENRE_ID " +
                "ORDER BY g.ID ASC";
        Map<Long, Set<Genre>> filmGenres = new HashMap<>();

        jdbc.query(sql, rs -> {
                Long filmId = rs.getLong("film_id");
                Genre genre = Genre.builder()
                        .id(rs.getLong("genre_id"))
                        .name(rs.getString("genre_name"))
                        .build();
            if (!rs.wasNull()) {
                filmGenres.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
                log.debug("Film ID  {} set genre {}", filmId, genre.getId());
            } else {
                log.debug("Film ID {} has no genres.", filmId);
            }
        });

        return filmGenres;
    }




}
