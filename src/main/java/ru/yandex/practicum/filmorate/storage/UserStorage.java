package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    Collection<User> getUsers();

    User getUserById(Long id);

    User deleteUserById(Long id);

    User createUser(User user);

    User updateUser(User user);

    void addFriend(Long id1, Long id2);

    void removeFriend(Long id1, Long id2);

    Set<User> findFriends(Long id);
}