package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    /**
     * Users Map
     */
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = users.get(newUser.getId());
        users.put(oldUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
