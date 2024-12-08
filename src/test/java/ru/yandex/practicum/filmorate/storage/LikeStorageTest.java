package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeStorage.class})
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
class LikeStorageTest {

    private final LikeStorage likeStorage; // `final` ensures injection

    @Test
    public void testGetLikes() {
        Set<Long> likes = likeStorage.getFilmLikes(1L);

        assertThat(likes).containsExactly(1L);
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
