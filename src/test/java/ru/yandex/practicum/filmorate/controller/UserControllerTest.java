package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    @Test
    public void postUser() {
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        assertThrows(ValidationException.class, () -> userController.addUser(new User(" ", "123",
                "123", LocalDate.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("adsasad", "21",
                "31", LocalDate.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("ad@m.a", "  ",
                "asd", LocalDate.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("ad@m.a", "as ",
                " ", LocalDate.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("ad@m.a", "qwe",
                "qwe", LocalDate.now().plusDays(2))));
    }

    @Test
    public void updateUser() {
        UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

        userController.addUser(new User("mail@mail.ru", "login", "name",
                LocalDate.now().minusDays(1)));
        assertThrows(ValidationException.class, () -> userController.updateUser(new User("mail@mail.ru",
                "login", "name", LocalDate.now().minusDays(2))));
    }
}