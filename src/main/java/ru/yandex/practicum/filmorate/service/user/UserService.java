package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Retrieves all users.
     *
     * @return Collection<User>
     */
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return User
     * @throws ValidationException
     */
    public User create(User user) {
        log.trace("Checking if user name is empty; setting login as name if true.");
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

    /**
     * Updates an existing user.
     *
     * @param newUser the user data to update
     * @return User
     * @throws NotFoundException
     */
    public User update(User newUser) {
        log.trace("Checking if user ID is present in storage.");
        if (userStorage.getById(newUser.getId()).isPresent()) {
            log.trace("Updating user in storage.");
            return userStorage.update(newUser);
        }
        throw new NotFoundException("User not found!");
    }

    /**
     * Adds a friend to the user friend list.
     *
     * @param userId
     * @param friendId
     * @return Map<String, String>
     * @throws NotFoundException
     * @throws ValidationException
     */
    public Map<String, String> addFriend(Long userId, Long friendId) {
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
        return Map.of("friend", "You added %s as a friend".formatted(friend.get().getName()));
    }

    /**
     * Removes a friend from the user's friend list.
     *
     * @param userId
     * @param friendId
     * @return Map<String, String>
     * @throws NotFoundException
     * @throws ValidationException
     */
    public Map<String, String> removeFriend(Long userId, Long friendId) {
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
            return Map.of("friends", "You removed %s from friends".formatted(friend.get().getName()));
        } catch (DataAccessException e) {
            log.error("Failed to remove friendship.", e);
            throw new ValidationException("Failed to remove friendship: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of common friends between two users.
     *
     * @param userId
     * @param otherId
     * @return Collection<User>
     * @throws NotFoundException
     */
    public Collection<User> getCommonFriend(Long userId, Long otherId) {
        if (!usersIdsValidation(userId, otherId)) {
            log.error("One or both user IDs are invalid.");
            throw new NotFoundException("One or both users not found!");
        }

        return userStorage.getCommonFriends(userId, otherId);
    }

    /**
     * Retrieves the friend list of a user.
     *
     * @param userId
     * @return Collection<User>
     * @throws NotFoundException
     */
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

    /**
     * Validates both user IDs exist.
     *
     * @param userId1
     * @param userId2
     * @return boolean
     */
    private boolean usersIdsValidation(Long userId1, Long userId2) {
        Optional<User> user1 = userStorage.getById(userId1);
        Optional<User> user2 = userStorage.getById(userId2);

        return user1.isPresent() && user2.isPresent();
    }

    /**
     * Generates new unique ID for a user.
     *
     * @return Long
     */
    private Long generateId() {
        Long currentId = userStorage.getAll().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentId;
    }

    /**
     * Deletes a user and all related data by user ID.
     *
     * @param userId ID of the user to be deleted
     * @throws NotFoundException if the user does not exist
     */
    public void deleteById(long userId) {
        log.debug("Attempting to delete user with ID {}", userId);
        if (userStorage.getById(userId).isEmpty()) {
            log.warn("User with ID {} not found", userId);
            throw new NotFoundException("User with ID " + userId + " not found");
        }
        log.trace("Deleting user ID {}", userId);
        userStorage.deleteById(userId);
        log.info("Successfully deleted user with ID {}", userId);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId ID of the user to retrieve
     * @return the user object if found
     * @throws NotFoundException if the user does not exist
     */
    public Optional<User> getUserById(long userId) {
        log.debug("Attempting to retrieve user with ID {}", userId);
        return userStorage.getById(userId);
    }
}
