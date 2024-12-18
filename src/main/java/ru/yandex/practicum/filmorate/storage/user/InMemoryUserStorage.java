package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    /**
     * Users Map
     */
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, List<Long>> friendships = new HashMap<>();


    @Override
    public Optional<User> getById(long id) throws NotFoundException {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID - %d не найден.".formatted(id));
        }
        return Optional.of(user);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
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
    public void addFriendship(Long userId, Long friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new NotFoundException("Один из пользователей не найден.");
        }


        if (friendships.containsKey(userId)) {
            List<Long> friendList = friendships.get(userId);
            if (friendList.contains(friendId)) {
                throw new UserAlreadyExistException("The user with id %d is already in your friends!".formatted(friendId));
            }
            friendList.add(friendId);
        }

        List<Long> friendsList = new ArrayList<>();
        friendsList.add(friendId);
        friendships.put(userId, friendsList);

    }

    @Override
    public List<User> getFriends(Long userId) {
        return friendships.get(userId).stream().map(
                friendId -> getById(friendId).get()
        ).collect(Collectors.toList());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        List<Long> userFriends = friendships.get(userId);
        if (userFriends != null) {
            if (!userFriends.contains(friendId)) {
                throw new NotFoundException("User with id %d is not in your friends".formatted(friendId));
            }
            userFriends.remove(friendId);
        }
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        List<Long> userFriends = friendships.getOrDefault(userId, Collections.emptyList());
        List<Long> friendFriends = friendships.getOrDefault(friendId, Collections.emptyList());


        return userFriends.stream()
                .filter(friendFriends::contains)
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
