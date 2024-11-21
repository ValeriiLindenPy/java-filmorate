package ru.yandex.practicum.filmorate.service.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;



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
        if (userStorage.getById(newUser.getId()).isPresent()) {
            log.trace("add new user in users");
            return userStorage.update(newUser);
        }
        throw new NotFoundException("Пользователь не найден!");
    }

    public Map<String,String> addFriend(Long userId, Long friendId) {

        log.trace("Validating user IDs.");
        Optional<User> user = userStorage.getById(userId);
        Optional<User> friend = userStorage.getById(friendId);

        if (!user.isPresent() || !friend.isPresent()) {
            log.error("One or both user IDs are invalid.");
            throw new NotFoundException("One or both users not found!");
        }

        log.trace("Attempting to add friendship.");
        try {
            userStorage.addFriendship(userId, friendId);
            log.info("Friendship successfully added: {} -> {}", userId, friendId);
        } catch (DataAccessException e) {
            log.error("Failed to add friendship.", e);
            throw new ValidationException("Failed to add friendship: " + e.getMessage());
        }
        return Map.of("friend", "Вы добавили %s в друзья".formatted(friend.get().getName()));
    }

    public Map<String,String> removeFriend(Long userId, Long friendId) {

        log.trace("Validating user IDs.");
        Optional<User> user = userStorage.getById(userId);
        Optional<User> friend = userStorage.getById(friendId);

        if (!user.isPresent() || !friend.isPresent()) {
            log.error("One or both user IDs are invalid.");
            throw new NotFoundException("One or both users not found!");
        }

        log.trace("Attempting to remove friendship.");
        try {
            userStorage.removeFriend(userId, friendId);
            log.info("Friendship successfully removed: {} -> {}", userId, friendId);
            return Map.of("friends", "Вы удалили %s из друзей".formatted(friend.get().getName()));
        } catch (DataAccessException e) {
            log.error("Failed to remove friendship.", e);
            throw new ValidationException("Failed to remove friendship: " + e.getMessage());
        }
    }

    public Collection<User> getCommonFriend(Long userId, Long otherId) {

        if (!usersIdsValidation(userId, otherId)) {
            log.error("One or both user IDs are invalid.");
            throw new NotFoundException("One or both users not found!");
        }

        return userStorage.getCommonFriends(userId, otherId);
    }

    public Collection<User> getFriends(Long userId) {
        log.trace("Validating user ID.");
        if (!userStorage.getById(userId).isPresent()) {
            log.error("User ID {} not found.", userId);
            throw new NotFoundException("User not found!");
        }

        log.trace("Fetching friends for user ID: {}", userId);
        Collection<User> friends = userStorage.getFriends(userId);

        log.info("Found {} friends for user ID: {}", friends.size(), userId);
        return friends;
    }

    private boolean usersIdsValidation(Long userId1, Long userId2) {
        Optional<User> user = userStorage.getById(userId1);
        Optional<User> friend = userStorage.getById(userId1);

        if (!user.isPresent() || !friend.isPresent()) {
            return false;
        }
        return true;
    }

    private Long generateId() {
        Long currentId = userStorage.getAll().stream()
                .mapToLong(user -> user.getId())
                .max()
                .orElse(0);
        return ++currentId;
    }
}
