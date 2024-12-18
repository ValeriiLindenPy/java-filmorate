package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
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
@AutoConfigureTestDatabase
public class UserDbStorageTest {

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

        User user2 = User.builder()
                .id(2L)
                .email("example2@example.com")
                .login("exampleUser2")
                .name("Example Name2")
                .password("password1234")
                .birthday(LocalDate.of(1994, 1, 1))
                .build();

        userStorage.create(user);
        userStorage.create(user2);
    }


    @Test
    public void testGetById() {
        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(1L);
                    assertThat(user.getName()).isEqualTo("Example Name");
                });
    }

    @Test
    public void testGetAll() {
        List<User> users = userStorage.getAll();

        assertThat(users).hasSize(2);
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setId(3L);
        user.setName("New User");
        user.setLogin("new123");
        user.setEmail("new123@gmail.com");
        user.setPassword("pas321");
        user.setBirthday(LocalDate.of(1996, 1, 1));

        userStorage.create(user);

        Optional<User> createdUser = userStorage.getById(3L);

        assertThat(createdUser)
                .isPresent()
                .hasValueSatisfying(userData -> {
                    assertThat(userData.getId()).isEqualTo(3L);
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
