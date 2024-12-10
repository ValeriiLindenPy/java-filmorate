package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Repository
@RequiredArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbc;

    public void addLike(Long userId, Long filmId) {
        String addLikeQuery = "INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)";
        jdbc.update(addLikeQuery, userId, filmId);
    }

    public void removeLike(Long userId, Long filmId) {
        String deleteLikeQuery = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbc.update(deleteLikeQuery, userId, filmId);
    }

    public Set<Long> getFilmLikes(Long filmId) {
        String getLikesQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(getLikesQuery, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    public Map<Long, Set<Long>> getAllFilmLikes() {
        String sql = """
                SELECT f.id AS film_id, fl.user_id AS user_id
                FROM films AS f
                LEFT JOIN film_likes AS fl ON fl.film_id = f.id;
                """;
        Map<Long, Set<Long>> filmsLikes = new HashMap<>();

        jdbc.query(sql, rm -> {
            Long filmId = rm.getLong("film_id");
            Long userId = rm.getLong("user_id");

            filmsLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        return filmsLikes;
    }
}
