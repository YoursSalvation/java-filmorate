package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserApiDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserApiDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Set<UserApiDto> findMutualFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        return userService.findMutualFriends(userId, otherId);
    }

    @GetMapping("/{userId}/friends")
    public Set<UserApiDto> findFriends(@PathVariable Long userId) {
        return userService.findFriends(userId);
    }

    @DeleteMapping("/{id}")
    public UserApiDto deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }

    @PostMapping
    public UserApiDto addUser(@RequestBody UserApiDto user) {
        return userService.createUser(user);
    }

    @PutMapping
    public UserApiDto updateUser(@RequestBody UserApiDto user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.removeFriend(userId, friendId);
    }
}