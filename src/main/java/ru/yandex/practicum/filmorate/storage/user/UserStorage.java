package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {

    /**
     * get a user by id in storage
     */
    User getById(Long id);

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

    /**
     * delete all users in storage
     */
    void clear();
}
