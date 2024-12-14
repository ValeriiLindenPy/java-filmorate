package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmsSearchBy;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final FilmRowMapper mapper;
    private final JdbcTemplate jdbc;
    private static final String BASE_FILM_QUERY = """
                SELECT f.*, mr.ID AS mpa_id, mr.name AS mpa_name
                FROM FILMS f
                LEFT JOIN (SELECT fl.film_id, COUNT(fl.user_id) AS likes
                      FROM FILM_LIKES fl
                      GROUP BY fl.film_id) likes_count ON f.ID = likes_count.film_id
                LEFT JOIN FILM_GENRES fg ON f.ID = fg.film_id
                LEFT JOIN MPA_RATINGS mr ON f.MPA_ID = mr.ID
            """;

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
    public List<Film> getAll() {
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
    public List<Film> getTop(int count) {
        String query = BASE_FILM_QUERY + """
                    ORDER BY likes_count.likes DESC
                    LIMIT ?;
                """;
        return jdbc.query(query, mapper, count).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Film> getTopByYear(int count, int year) {
        String query = BASE_FILM_QUERY + """
                    WHERE EXTRACT(YEAR FROM f.release_date) = ?
                    ORDER BY likes_count.likes DESC
                    LIMIT ?;
                """;
        return jdbc.query(query, mapper, year, count).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Film> getTopByGenre(int count, int genreId) {
        String query = BASE_FILM_QUERY + """
                    WHERE fg.genre_id = ?
                    ORDER BY likes_count.likes DESC
                    LIMIT ?;
                """;
        return jdbc.query(query, mapper, genreId, count).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<Film> getTopYearAndGenre(int count, int genreId, int year) {
        String query = BASE_FILM_QUERY + """
                    WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ?
                    ORDER BY likes_count.likes DESC
                    LIMIT ?;
                """;
        return jdbc.query(query, mapper, genreId, year, count).stream().distinct().collect(Collectors.toList());
    }

    public List<Film> getDirectorFilmSortedByLike(Long directorId) {
        String getDirectorFilmSortedByLikeQuery = "SELECT f.*, fl.likes_count, mr.id AS mpa_id, mr.name AS mpa_name\n" +
                "FROM films f\n" +
                "LEFT JOIN (\n" +
                "    SELECT film_id, COUNT(user_id) AS likes_count\n" +
                "    FROM film_likes\n" +
                "    GROUP BY film_id\n" +
                ") fl ON fl.film_id = f.id\n" +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.id\n" +
                "WHERE f.id IN (\n" +
                "    SELECT film_id\n" +
                "    FROM film_directors fd \n" +
                "    WHERE fd.director_id = ?\n" +
                ")\n" +
                "ORDER BY fl.likes_count DESC";

        return jdbc.query(getDirectorFilmSortedByLikeQuery, mapper, directorId);
    }

    public List<Film> getDirectorFilmSortedByYear(Long directorId) {
        String getDirectorFilmSortedByYearQuery = "SELECT f.*,\n" +
                "EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS DATE)) AS release_year,\n" +
                "mr.ID AS mpa_id, mr.name AS mpa_name\n" +
                "FROM FILMS f\n" +
                "LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.id\n" +
                "WHERE f.ID IN (\n" +
                "\tSELECT film_id\n" +
                "\tFROM FILM_DIRECTORS fd \n" +
                "\tWHERE fd.director_id = ?\n" +
                ")\n" +
                "ORDER BY release_year ASC";

        return jdbc.query(getDirectorFilmSortedByYearQuery, mapper, directorId);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String commonFilmsQuery = """
                SELECT f.*, mr.name AS mpa_name, COUNT(fl.user_id) AS likes
                FROM films f
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                LEFT JOIN mpa_ratings mr ON f.mpa_id = mr.id
                WHERE f.id IN (
                    SELECT fl1.film_id
                    FROM film_likes fl1
                    JOIN film_likes fl2 ON fl1.film_id = fl2.film_id
                    WHERE fl1.user_id = ? AND fl2.user_id = ?
                )
                GROUP BY f.id, mr.name
                ORDER BY likes DESC;
                """;

        return jdbc.query(commonFilmsQuery, mapper, userId, friendId);
    }

    public List<Film> searchByParam(String query, FilmsSearchBy param) {
        String partOrder = " ORDER BY cnt DESC";
        String partWhereClause = "";
        switch (param) {
            case TITLE -> partWhereClause = " WHERE f.NAME ILIKE CONCAT('%',?,'%')";
            case DIRECTOR -> partWhereClause = " WHERE d.NAME ILIKE CONCAT('%',?,'%')";
            case ALL -> partWhereClause = """
                    WHERE f.NAME ILIKE CONCAT('%',?,'%') OR d.NAME ILIKE CONCAT('%',?,'%')
                    """;
        }
        String findFilmsByParamQuery = """
                SELECT DISTINCT f.ID
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
