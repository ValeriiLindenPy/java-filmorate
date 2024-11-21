package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.storage.UserSqlQueries.*;


@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public Optional<User> getById(Long id) {
        try {
            User user = jdbc.queryForObject(FIND_USER_BY_ID_QUERY, mapper, id);
            return Optional.of(user);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        return jdbc.query(FIND_ALL_USERS_QUERY, mapper);
    }

    @Override
    public User create(User user) {
        jdbc.update(INSERT_USER_QUERY,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getPassword(),
                user.getBirthday()
        );
        return user;
    }

    @Override
    public User update(User newUser) {
        int rowsUpdated = jdbc.update(UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getPassword(),
                newUser.getBirthday(),
                newUser.getId()
        );


        if (rowsUpdated == 0) {
            throw new DataAccessException("User with ID " + newUser.getId() + " not found.") {
            };
        }

        return newUser;
    }

    @Override
    public void deleteById(Long id) {
        int rowsDeleted = jdbc.update(DELETE_USER_QUERY, id);

        if (rowsDeleted == 0) {
            throw new DataAccessException("User with ID " + id + " not found.") {
            };
        }
    }

    public void addFriendship(Long userId, Long friendId) {

        int count = jdbc.queryForObject(CHECK_FRIENDSHIP_QUERY, Integer.class, userId, friendId);

        if (count == 0) {
            jdbc.update(INSERT_FRIENDSHIP_QUERY, userId, friendId);
        }
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        return jdbc.query(GET_COMMON_FRIENDSHIP_QUERY, mapper, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return jdbc.query(GET_FRIENDS_QUERY, mapper, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        int rowsDeleted = jdbc.update(DELETE_FRIENDSHIP_QUERY, userId, friendId);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Friendship not found or already removed!");
        }
    }
}
