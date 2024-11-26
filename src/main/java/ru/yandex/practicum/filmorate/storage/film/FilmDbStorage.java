package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final FilmRowMapper mapper;
    private final MPAStorage mpaStorage;
    private final JdbcTemplate jdbc;

    @Override
    public Optional<Film> getById(Long id) {
        try {
            String findFilmByIdQuery = "SELECT * FROM films WHERE id = ?";
            Film film = jdbc.queryForObject(findFilmByIdQuery, mapper, id);
            if (film != null) {
                setFilmGenres(film);
                setFilmMPA(film);
            }
            return Optional.of(film);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getAll() {
        String findAllFilmsQuery = "SELECT * FROM films";
        return jdbc.query(findAllFilmsQuery, mapper);
    }

    @Override
    public Film create(Film film) {
        String insertFilmQuery = "INSERT INTO films (id, name, description, duration, release_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbc.update(insertFilmQuery,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());
        saveMPA(film);
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        String updateFilmQuery = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ? WHERE id = ?";
        jdbc.update(updateFilmQuery,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getDuration(),
                newFilm.getReleaseDate(),
                newFilm.getId()
        );

        updateMPA(newFilm);
        updateGenres(newFilm);
        return newFilm;
    }

    @Override
    public void deleteById(Long id) {
        String deleteFilmQuery = "DELETE FROM films WHERE id = ?";
        jdbc.update(deleteFilmQuery, id);
    }

    @Override
    public Set<Long> getLikes(Long filmId) {
        String getLikesQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(getLikesQuery, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    @Override
    public Collection<Film> getTop(int count) {
        String getLikesPopularQuery = "SELECT f.*\n" +
                "FROM FILMS f\n" +
                "JOIN (\n" +
                "    SELECT fl.film_id, COUNT(fl.user_id) AS likes\n" +
                "    FROM FILM_LIKES fl\n" +
                "    GROUP BY fl.film_id\n" +
                ") likes_count ON f.ID = likes_count.film_id\n" +
                "ORDER BY likes_count.likes DESC\n" +
                "LIMIT ?;";
        return jdbc.query(getLikesPopularQuery, mapper, count);
    }

    public void addLike(Long userId, Long filmId) {
        String addLikeQuery = "INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)";
        jdbc.update(addLikeQuery, userId, filmId);
    }

    @Override
    public void removeLike(Long userId, Long filmId) {
        String deleteLikeQuery = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbc.update(deleteLikeQuery, userId, filmId);
    }

    private void saveGenres(Film film) {
        String insertGenresQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> jdbc.update(insertGenresQuery, film.getId(), genre.getId()));
        }
    }

    private void updateGenres(Film film) {
        String updateGenresQuery = "UPDATE film_genres SET genre_id = ? WHERE film_id = ?";
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> jdbc.update(updateGenresQuery, film.getId(), genre.getId()));
        }
    }

    private void saveMPA(Film film) {
        if (film.getMpa() != null) {
            String insertFilmMpaQuery = "INSERT INTO film_mpa (film_id, mpa_id) VALUES (?, ?)";
            jdbc.update(insertFilmMpaQuery, film.getId(), film.getMpa().getId());
        }
    }

    private void updateMPA(Film film) {
        String updateFilmMpaQuery = "UPDATE film_mpa SET mpa_id = ? WHERE film_id = ?";
        jdbc.update(updateFilmMpaQuery, film.getMpa().getId(), film.getId());
    }

    private void setFilmGenres(Film film) {
        String getFilmGenresQuery = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbc.query(getFilmGenresQuery, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new HashSet<>(genres));
    }

    private void setFilmMPA(Film film) {
        String getFilmMpaQuery = "SELECT mr.id, mr.name FROM mpa_ratings mr \n" +
                "JOIN film_mpa fm ON mr.id = fm.MPA_ID \n" +
                "WHERE fm.film_id = ?";
        MPA mpa = jdbc.queryForObject(getFilmMpaQuery, (rs, rowNum) -> {
            MPA mpaFilm = new MPA();
            mpaFilm.setId(rs.getLong("id"));
            mpaFilm.setName(rs.getString("name"));
            return mpaFilm;
        }, film.getId());
        film.setMpa(mpa);
    }
}
