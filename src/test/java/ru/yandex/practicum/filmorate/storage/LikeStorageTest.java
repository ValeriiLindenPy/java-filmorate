package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeStorage.class, UserDbStorage.class,
        FilmDbStorage.class, UserRowMapper.class,
        FilmRowMapper.class})
@AutoConfigureTestDatabase
class LikeStorageTest {

    private final LikeStorage likeStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .email("example@example.com")
                .login("exampleUser")
                .name("Example Name")
                .password("password123")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Film film = Film.builder()
                .id(1L)
                .name("Inception")
                .description("A skilled thief is offered a chance to have his past crimes forgiven.")
                .mpa(new MPA(1L, "G")) // Assuming MPA has a constructor like MPA(int id, String name)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Matrix")
                .description("Neo")
                .mpa(new MPA(1L, "G")) // Assuming MPA has a constructor like MPA(int id, String name)
                .releaseDate(LocalDate.of(2000, 7, 16))
                .duration(120)
                .build();

        filmStorage.create(film);
        filmStorage.create(film2);
        userStorage.create(user);
    }

    @Test
    public void testAddLike() {
        likeStorage.addLike(1L, 1L);

        Set<Long> likes = likeStorage.getFilmLikes(1L);

        assertThat(likes).containsExactlyInAnyOrder(1L);
    }

    @Test
    public void testRemoveLike() {
        likeStorage.removeLike(1L, 1L);

        Set<Long> likes = likeStorage.getFilmLikes(1L);

        assertThat(likes).isEmpty();
    }
}
