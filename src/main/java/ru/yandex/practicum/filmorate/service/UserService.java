package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User deleteUserById(Long id) {
        return userStorage.deleteUserById(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(Long id1, Long id2) {
        if (id1 == null || id1 < 1) throw new NotFoundException("Некорректный id");
        if (id2 == null || id2 < 1) throw new NotFoundException("Некорректный id");
        if (Objects.equals(id1, id2)) throw new ValidationException("id должны различаться");
        userStorage.addFriend(id1, id2);
    }

    public void removeFriend(Long id1, Long id2) {
        if (id1 == null || id1 < 1) throw new NotFoundException("Некорректный id");
        if (id2 == null || id2 < 1) throw new NotFoundException("Некорректный id");
        if (Objects.equals(id1, id2)) throw new ValidationException("id должны различаться");
        userStorage.removeFriend(id1, id2);
    }

    public Set<User> findMutualFriends(Long id1, Long id2) {
        if (id1 == null || id2 == null || id1 < 1 || id2 < 1) throw new IllegalArgumentException("Некорректный id");
        if (Objects.equals(id1, id2)) throw new ValidationException("id должны различаться");
        Set<User> mutualFriends = new HashSet<>();
        Set<Long> friendsUser = userStorage.getUserById(id1).getFriends();
        Set<Long> friendsOtherUser = userStorage.getUserById(id2).getFriends();
        for (Long id : friendsUser) {
            if (friendsOtherUser.contains(id)) mutualFriends.add(userStorage.getUserById(id));
        }
        return mutualFriends;
    }
}