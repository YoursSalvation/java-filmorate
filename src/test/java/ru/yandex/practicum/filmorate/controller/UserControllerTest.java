package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    @Test
    public void postUser() {
        UserController userController = new UserController();

        assertThrows(ValidationException.class, () -> userController.addUser(new User(" ", "123",
                "123", LocalDateTime.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("adsasad", "21",
                "31", LocalDateTime.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("ad@m.a", "  ",
                "asd", LocalDateTime.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("ad@m.a", "as ",
                " ", LocalDateTime.now().minusDays(2))));
        assertThrows(ValidationException.class, () -> userController.addUser(new User("ad@m.a", "qwe",
                "qwe", LocalDateTime.now().plusDays(2))));
    }

    @Test
    public void updateUser() {
        UserController userController = new UserController();

        userController.addUser(new User("mail@mail.ru", "login", "name",
                LocalDateTime.now().minusDays(1)));
        assertThrows(ValidationException.class, () -> userController.updateUser(new User("mail@mail.ru",
                "login", "name", LocalDateTime.now().minusDays(2))));
    }
}