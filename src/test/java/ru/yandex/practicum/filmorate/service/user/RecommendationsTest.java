package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.like.LikeService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.*;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, UserDbStorage.class, UserRowMapper.class, FilmService.class, FilmDbStorage.class,
        FilmRowMapper.class, LikeService.class, LikeStorage.class, MPAStorage.class, MPARowMapper.class,
        GenreStorage.class, GenreRowMapper.class, DirectorStorage.class, DirectorRowMapper.class})
@AutoConfigureTestDatabase
class RecommendationsTest {
    private final UserService userService;
    private final FilmService filmService;
    private final LikeService likeService;

    @Test
    void filmRecommendationIfTheSameLike() {
        User user1 = new User(null, "email1", "login1", "name1", "pass1", LocalDate.now().minusDays(1));
        User user2 = new User(null, "email2", "login2", "name2", "pass2", LocalDate.now().minusDays(1));

        user1 = userService.create(user1);
        user2 = userService.create(user2);

        Film film1 = new Film();
        film1.setId(6L);
        film1.setName("New Film");
        film1.setDescription("Description of new film");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(2023, 1, 1));
        film1.setMpa(new MPA(1L, "G"));

        Film film2 = new Film();
        film2.setId(6L);
        film2.setName("New Film");
        film2.setDescription("Description of new film");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        film2.setMpa(new MPA(1L, "G"));

        film1 = filmService.create(film1);
        film2 = filmService.create(film2);

        likeService.addLike(film1.getId(), user1.getId());
        likeService.addLike(film1.getId(), user2.getId());
        likeService.addLike(film2.getId(), user2.getId());

        List<Film> recommendations = userService.getRecommendations(user1.getId());

        assertEquals(1, recommendations.size());
        assertEquals(film2.getId(), recommendations.get(0).getId());
    }
}