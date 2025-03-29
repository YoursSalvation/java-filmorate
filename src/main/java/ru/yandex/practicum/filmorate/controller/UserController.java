package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) throw new ValidationException("Адрес" +
                " электронный почты не может быть пустым и должен содержать символ '@'");
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) throw new ValidationException("Логин не может" +
                " быть пустым и содержать пробелы");
        if (user.getBirthday().isAfter(LocalDateTime.now()))
            throw new ValidationException("Дата рождения не может быть в будущем");
        if (user.getName().isBlank()) user.setName(user.getLogin());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) throw new ValidationException("Id должен быть указан");
        User actualUser = users.get(user.getId());
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) user.setEmail(actualUser.getEmail());
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) user.setLogin(actualUser.getLogin());
        if (user.getBirthday().isAfter(LocalDateTime.now())) user.setBirthday(actualUser.getBirthday());
        if (user.getName().isBlank()) user.setName(user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    private Long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
    }
}