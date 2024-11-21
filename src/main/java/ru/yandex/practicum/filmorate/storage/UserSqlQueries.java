package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

@Component
public class UserSqlQueries {
    public static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    public static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    public static final String INSERT_USER_QUERY = "INSERT INTO users(id, email, login, name, password, birthday)" +
            "VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, password = ?, birthday = ? WHERE id = ?";
    public static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    public static final String INSERT_FRIENDSHIP_QUERY =
            "INSERT INTO user_friendships (user1_id, user2_id) VALUES (?, ?)";
    public static final String GET_FRIENDS_QUERY =
            "SELECT u.* FROM users u " +
                    "JOIN user_friendships uf ON u.id = uf.user2_id " +
                    "WHERE uf.user1_id = ?";
    public static final String DELETE_FRIENDSHIP_QUERY =
            "DELETE FROM user_friendships WHERE user1_id = ? AND user2_id = ?";
    public static final String CHECK_FRIENDSHIP_QUERY = "SELECT COUNT(*) FROM user_friendships WHERE user1_id = ? AND user2_id = ?";
    public static final String GET_COMMON_FRIENDSHIP_QUERY = "SELECT * FROM USERS u \n" +
            "WHERE u.ID IN(\n" +
            "SELECT uf1.user2_id AS common_friend_id\n" +
            "FROM user_friendships uf1\n" +
            "JOIN user_friendships uf2 ON uf1.USER2_ID = uf2.USER2_ID\n" +
            "WHERE uf1.USER1_ID = ? AND uf2.USER1_ID = ?)";

}
