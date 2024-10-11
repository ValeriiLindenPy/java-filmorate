package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationMarker.OnCreate.class)
    public User create(@Valid @RequestBody User user) {
        log.trace("check user name.");
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is empty - set login as name.");
            user.setName(user.getLogin());
        }
        log.trace("set id.");
        user.setId(generateId());
        log.trace("add instance in store");
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    @Validated(ValidationMarker.OnUpdate.class)
    public User update(@Valid @RequestBody User newUser) {
        log.trace("check if user id in users");
        if (users.containsKey(newUser.getId())) {
            log.debug("making old user ref.");
            User oldUser = users.get(newUser.getId());
            log.debug("check new login is already in users");
            if (!oldUser.getLogin().equals(newUser.getLogin())  && users.values().stream()
                    .anyMatch(user -> user.getLogin().equals(newUser.getLogin()))) {
                log.warn("User with login {} is already in users", newUser.getLogin());
                throw new ValidationException("Пользователь с таким логином уже есть!");
            }
            log.trace("add new user in users");
            users.put(newUser.getId(), newUser);
            return newUser;
        }
        throw new ValidationException("Пользователь не найден!");
    }

    private Long generateId() {
        Long currentId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
