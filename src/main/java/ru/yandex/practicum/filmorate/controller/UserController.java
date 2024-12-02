package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationMarker;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriend(@PathVariable long id,
                                            @PathVariable long otherId) {
        return userService.getCommonFriend(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Validated(ValidationMarker.OnCreate.class) @Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Map<String, String> addFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.addFriend(id, friendId);
    }


    @PutMapping
    public User update(@Validated(ValidationMarker.OnUpdate.class) @Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Map<String,String> removeFriend(@PathVariable long id, @PathVariable long friendId) {
        return userService.removeFriend(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        userService.deleteById(userId);
    }
}
