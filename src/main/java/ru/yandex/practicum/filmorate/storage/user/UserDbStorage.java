package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public Optional<User> getById(long id) {
        try {
            String findUserByIdQuery = "SELECT * FROM users WHERE id = ?";
            User user = jdbc.queryForObject(findUserByIdQuery, mapper, id);
            return Optional.of(user);
        } catch (DataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        String findAllUsersQuery = "SELECT * FROM users";
        return jdbc.query(findAllUsersQuery, mapper);
    }

    @Override
    public User create(User user) {
        String insertUserQuery = "INSERT INTO users(id, email, login, name, password, birthday)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbc.update(insertUserQuery,
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
        String updateUserQuery = "UPDATE users SET email = ?, login = ?, name = ?, password = ?, birthday = ? WHERE id = ?";
        jdbc.update(updateUserQuery,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getPassword(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public void deleteById(Long id) {
        String deleteUserQuery = "DELETE FROM users WHERE id = ?";
        int rowsDeleted = jdbc.update(deleteUserQuery, id);

        if (rowsDeleted == 0) {
            throw new DataAccessException("User with ID " + id + " not found.") {
            };
        }
    }

    public void addFriendship(Long userId, Long friendId) {
        String checkFriendshipQuery = "SELECT COUNT(*) FROM user_friendships WHERE user1_id = ? AND user2_id = ?";
        int count = jdbc.queryForObject(checkFriendshipQuery, Integer.class, userId, friendId);

        if (count == 0) {
            String insertFriendshipQuery = "INSERT INTO user_friendships (user1_id, user2_id) VALUES (?, ?)";
            jdbc.update(insertFriendshipQuery, userId, friendId);
        }
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        String getCommonFriendshipQuery = "SELECT * FROM USERS u \n" +
                "WHERE u.ID IN(\n" +
                "SELECT uf1.user2_id AS common_friend_id\n" +
                "FROM user_friendships uf1\n" +
                "JOIN user_friendships uf2 ON uf1.USER2_ID = uf2.USER2_ID\n" +
                "WHERE uf1.USER1_ID = ? AND uf2.USER1_ID = ?)";
        return jdbc.query(getCommonFriendshipQuery, mapper, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String getFriendsQuery =
                "SELECT u.* FROM users u " +
                        "JOIN user_friendships uf ON u.id = uf.user2_id " +
                        "WHERE uf.user1_id = ?";
        return jdbc.query(getFriendsQuery, mapper, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String deleteFriendshipQuery =
                "DELETE FROM user_friendships WHERE user1_id = ? AND user2_id = ?";
        jdbc.update(deleteFriendshipQuery, userId, friendId);
    }
}
