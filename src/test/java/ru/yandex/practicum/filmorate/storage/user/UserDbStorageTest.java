package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
public class UserDbStorageTest {

    private final UserDbStorage userStorage;


    @Test
    public void testGetById() {
        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(1L);
                    assertThat(user.getName()).isEqualTo("Alice Johnson");
                    assertThat(user.getEmail()).isEqualTo("alice@example.com");
                    assertThat(user.getLogin()).isEqualTo("alice123");
                    assertThat(user.getPassword()).isEqualTo("password1");
                    assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 5, 10));
                });
    }

    @Test
    public void testGetAll() {
        List<User> users = userStorage.getAll();

        assertThat(users).hasSize(3);
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setId(6L);
        user.setName("New User");
        user.setLogin("new123");
        user.setEmail("new123@gmail.com");
        user.setPassword("pas321");
        user.setBirthday(LocalDate.of(1996, 1, 1));

        userStorage.create(user);

        Optional<User> createdUser = userStorage.getById(6L);

        assertThat(createdUser)
                .isPresent()
                .hasValueSatisfying(userData -> {
                    assertThat(userData.getId()).isEqualTo(6L);
                    assertThat(userData.getName()).isEqualTo("New User");
                    assertThat(userData.getEmail()).isEqualTo("new123@gmail.com");
                    assertThat(userData.getLogin()).isEqualTo("new123");
                    assertThat(userData.getPassword()).isEqualTo("pas321");
                    assertThat(userData.getBirthday()).isEqualTo(LocalDate.of(1996, 1, 1));
                });
    }

    @Test
    public void testUpdate() {
        Optional<User> optionalUser = userStorage.getById(1L);
        assertThat(optionalUser).isPresent();

        User user = optionalUser.get();
        user.setName("UpdatedNew User");
        user.setLogin("new124");
        user.setEmail("new124@gmail.com");
        user.setPassword("pas421");
        user.setBirthday(LocalDate.of(1996, 2, 1));

        userStorage.update(user);

        Optional<User> updatedUser = userStorage.getById(1L);

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(userData -> {
                    assertThat(userData.getName()).isEqualTo("UpdatedNew User");
                    assertThat(userData.getEmail()).isEqualTo("new124@gmail.com");
                    assertThat(userData.getLogin()).isEqualTo("new124");
                    assertThat(userData.getPassword()).isEqualTo("pas421");
                    assertThat(userData.getBirthday()).isEqualTo(LocalDate.of(1996, 2, 1));
                });
    }

    @Test
    public void testDeleteById() {
        Optional<User> optionalUser = userStorage.getById(1L);
        assertThat(optionalUser).isPresent();
        userStorage.deleteById(1L);
        Optional<User> deletedUser = userStorage.getById(1L);
        assertThat(deletedUser).isNotPresent();
    }
}
