package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.UserApiDto;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserService.class, UserDbStorage.class})
class UserServiceTest {

    private final UserService userService;

    @BeforeEach
    void setUp() {
        UserApiDto u1 = new UserApiDto();
        u1.setName("Dima");
        u1.setLogin("dima");
        u1.setEmail("dima@ya.ru");
        userService.createUser(u1);

        UserApiDto u2 = new UserApiDto();
        u2.setName("Denis");
        u2.setLogin("denis");
        u2.setEmail("denis@ya.ru");
        userService.createUser(u2);

        UserApiDto u3 = new UserApiDto();
        u3.setName("Maxim");
        u3.setLogin("maxim");
        u3.setEmail("maxim@ya.ru");
        userService.createUser(u3);
    }

    @Test
    void friendOps() {
        assertTrue(userService.findFriends(1L).isEmpty());
        assertTrue(userService.findFriends(2L).isEmpty());
        assertTrue(userService.findFriends(3L).isEmpty());

        userService.addFriend(1L, 2L);
        assertTrue(userService.findFriends(1L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findFriends(2L).isEmpty());
        assertTrue(userService.findFriends(3L).isEmpty());

        userService.addFriend(2L, 1L);
        assertTrue(userService.findFriends(1L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findFriends(2L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 1L));
        assertTrue(userService.findFriends(3L).isEmpty());

        assertTrue(userService.findMutualFriends(1L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 1L).isEmpty());
        assertTrue(userService.findMutualFriends(1L, 3L).isEmpty());
        assertTrue(userService.findMutualFriends(3L, 1L).isEmpty());
        assertTrue(userService.findMutualFriends(3L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 3L).isEmpty());

        userService.addFriend(3L, 2L);
        assertTrue(userService.findMutualFriends(1L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 1L).isEmpty());
        assertTrue(userService.findMutualFriends(1L, 3L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findMutualFriends(3L, 1L).stream().filter(Objects::nonNull).map(UserApiDto::getId).anyMatch(l -> l == 2L));
        assertTrue(userService.findMutualFriends(3L, 2L).isEmpty());
        assertTrue(userService.findMutualFriends(2L, 3L).isEmpty());

        userService.removeFriend(1L, 2L);
        userService.removeFriend(2L, 1L);
        userService.removeFriend(3L, 2L);
        assertTrue(userService.findFriends(1L).isEmpty());
        assertTrue(userService.findFriends(2L).isEmpty());
        assertTrue(userService.findFriends(3L).isEmpty());
    }

    @Test
    void getUsers() {
        Collection<UserApiDto> users = userService.getUsers();
        List<String> logins = users.stream().filter(Objects::nonNull).map(UserApiDto::getLogin).toList();
        assertEquals(3, users.size());
        assertTrue(logins.contains("dima"));
        assertTrue(logins.contains("denis"));
        assertTrue(logins.contains("maxim"));
    }

    @Test
    void CRUD() {
        UserApiDto user = new UserApiDto();
        user.setEmail("vlad@ya.ru");
        user.setLogin("vladislav");
        user.setName("Vlad");
        user.setBirthday(LocalDate.of(2003, 3, 17));

        UserApiDto createdUser = userService.createUser(user);
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getBirthday(), createdUser.getBirthday());

        Long id = createdUser.getId();

        UserApiDto nextUser = new UserApiDto();
        nextUser.setId(id);
        nextUser.setEmail("robert@ya.ru");
        nextUser.setLogin("robert");
        nextUser.setName("Robert");
        nextUser.setBirthday(LocalDate.of(2003, 10, 9));

        UserApiDto updatedUser = userService.updateUser(nextUser);
        assertEquals(nextUser.getId(), updatedUser.getId());
        assertEquals(nextUser.getEmail(), updatedUser.getEmail());
        assertEquals(nextUser.getLogin(), updatedUser.getLogin());
        assertEquals(nextUser.getName(), updatedUser.getName());
        assertEquals(nextUser.getBirthday(), updatedUser.getBirthday());

        UserApiDto receivedUser = userService.getUserById(id);
        assertEquals(nextUser.getId(), receivedUser.getId());
        assertEquals(nextUser.getEmail(), receivedUser.getEmail());
        assertEquals(nextUser.getLogin(), receivedUser.getLogin());
        assertEquals(nextUser.getName(), receivedUser.getName());
        assertEquals(nextUser.getBirthday(), receivedUser.getBirthday());

        userService.deleteUserById(id);
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(id);
        });
    }
}