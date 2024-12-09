package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmService filmService;

    /**
     * Retrieves all users.
     *
     * @return List<User>
     */
    public List<User> getAll() {
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
     * @return List<User>
     * @throws NotFoundException
     */
    public List<User> getCommonFriend(Long userId, Long otherId) {
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
     * @return List<User>
     * @throws NotFoundException
     */
    public List<User> getFriends(Long userId) {
        log.trace("Validating user ID.");
        if (!userStorage.getById(userId).isPresent()) {
            log.error("User ID {} not found.", userId);
            throw new NotFoundException("User not found!");
        }

        log.trace("Fetching friends for user ID: {}", userId);
        List<User> friends = userStorage.getFriends(userId);

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

    /**
     * Returns a list of recommended movies to watch.
     *
     * @param userId ID of the user
     * @return list of films
     * @throws NotFoundException if the user does not exist
     */
    public List<Film> getRecommendations(long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " not found");
        }

        /*
        Getting a table in the following format:

        {
            'userId1': [filmId1, filmId3],
            'userId2': [filmId2, filmId3],
            'userId3': [filmId1]
        }
         */

        Map<Long, Set<Long>> usersLikes = new HashMap<>();

        // Builds a map of user IDs to their liked film IDs.
        List<Film> movies = filmService.getAll();
        for (Film film : movies) {
            for (Long id : film.getLikes()) {
                usersLikes.computeIfAbsent(id, k -> new HashSet<>()).add(film.getId());
            }
        }

        if (!usersLikes.containsKey(userId)) {
            return Collections.emptyList();
        }

        // Finds the user whose liked films most closely match the given user's liked films.
        Long mostSimilarUserId = null;
        int maxOverlap = 0;

        for (Long id : usersLikes.keySet()) {
            // Do not compare with itself.
            if (id.equals(userId)) {
                continue;
            }

            Set<Long> intersection = new HashSet<>(usersLikes.get(userId));
            intersection.retainAll(usersLikes.get(id));
            int overlap = intersection.size();

            if (overlap > maxOverlap) {
                mostSimilarUserId = id;
                maxOverlap = overlap;
            }
        }

        if (mostSimilarUserId == null) {
            return Collections.emptyList();
        }

        // Creating a list of recommended movies
        final Long userWithGreatestMatch = mostSimilarUserId;
        return movies.stream()
                .filter(film -> usersLikes.get(userWithGreatestMatch).contains(film.getId()) && !usersLikes.get(userId).contains(film.getId()))
                .toList();
    }
}
