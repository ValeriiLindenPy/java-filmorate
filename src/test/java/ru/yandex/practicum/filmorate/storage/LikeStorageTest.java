package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeStorage.class})
@AutoConfigureTestDatabase
class LikeStorageTest {

    private final LikeStorage likeStorage; // `final` ensures injection

    @Test
    public void testGetLikes() {
        Set<Long> likes = likeStorage.getFilmLikes(1L);

    }

    @Test
    public void testAddLike() {
        likeStorage.addLike(2L, 1L);

        Set<Long> likes = likeStorage.getFilmLikes(1L);

        assertThat(likes).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    public void testRemoveLike() {
        likeStorage.removeLike(1L, 1L);

        Set<Long> likes = likeStorage.getFilmLikes(1L);

        assertThat(likes).isEmpty();
    }
}
