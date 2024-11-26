package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UserStorage {

    /**
     * get a user by id in storage
     */
    Optional<User> getById(long id);

    /**
     * get all users in storage
     */
    Collection<User> getAll();

    /**
     * create a user in storage
     */
    User create(User user);

    /**
     * update a user in storage
     */
    User update(User newUser);

    /**
     * delete a user by id from storage
     */
    void deleteById(Long id);

    void addFriendship(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    void removeFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long friendId);
}
