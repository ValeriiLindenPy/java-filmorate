package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.exception.NotFoundException;

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
    public User getById(Long id) throws NotFoundException {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID - %d не найден.".formatted(id));
        }
        return user;
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
