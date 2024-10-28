package ru.yandex.practicum.filmorate.service.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        log.trace("Checking if user name is empty, set login as name if true.");
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is empty - setting login as name.");
            user.setName(user.getLogin());
        }

        log.trace("Generating and setting user ID.");
        user.setId(generateId());

        log.trace("Attempting to create user in storage.");
        User createdUser = userStorage.create(user);
        if (createdUser == null) {
            log.error("Failed to create user: UserStorage returned null.");
            throw new ValidationException("Failed to create user.");
        }

        log.info("User successfully created with ID: {}", createdUser.getId());
        return createdUser;
    }

    public User update(User newUser) {
        log.trace("check if user id in users");
        if (userStorage.getById(newUser.getId()) != null) {
            log.trace("add new user in users");
            return userStorage.update(newUser);
        }
        throw new NotFoundException("Пользователь не найден!");
    }

    public Map<String,String> addFriend(Long userId, Long friendId) {

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user.getFriends().contains(friendId)) {
            return Map.of("friend", "%s уже у вас в друзьях".formatted(friend.getName()));
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return Map.of("friend", "Вы добавили %s в друзья".formatted(friend.getName()));
    }

    public Map<String,String> removeFriend(Long userId, Long friendId) {

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (!user.getFriends().contains(friendId)) {
            return Map.of("friends", "%s не у вас в друзьях".formatted(friend.getName()));
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return Map.of("friends", "Вы удалили %s из друзей".formatted(friend.getName()));
    }

    public Collection<User> getCommonFriend(Long userId, Long otherId) {

        User user = userStorage.getById(userId);
        User other = userStorage.getById(otherId);


        if (!user.getFriends().contains(otherId)) {
            throw new NotFoundException("%s не у вас в друзьях".formatted(other.getName()));
        }

        return user.getFriends().stream()
                .filter(friendId -> other.getFriends().contains(friendId))
                .map(friendId -> userStorage.getById(friendId))
                .collect(Collectors.toSet());
    }

    public Collection<User> getFriends(Long userId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("Пользователь с ID - %d не найден.".formatted(userId));
        }
        return userStorage.getById(userId).getFriends().stream()
                .map(id -> userStorage.getById(id))
                .collect(Collectors.toSet());
    }

    private Long generateId() {
        Long currentId = userStorage.getAll().stream()
                .mapToLong(user -> user.getId())
                .max()
                .orElse(0);
        return ++currentId;
    }
}
