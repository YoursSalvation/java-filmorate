package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) throw new NotFoundException("Пользователь не найден");
        return user;
    }

    @Override
    public User deleteUserById(Long id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) throw new ValidationException("Адрес" +
                " электронный почты не может быть пустым и должен содержать символ '@'");
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) throw new ValidationException("Логин не может" +
                " быть пустым и содержать пробелы");
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения не может быть в будущем");
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) throw new ValidationException("Id должен быть указан");
        if (!users.containsKey(user.getId())) throw new NotFoundException("Пользователь с указанным id не найден");
        User actualUser = users.get(user.getId());
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) user.setEmail(actualUser.getEmail());
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) user.setLogin(actualUser.getLogin());
        if (user.getBirthday().isAfter(LocalDate.now())) user.setBirthday(actualUser.getBirthday());
        if (user.getName().isBlank()) user.setName(user.getLogin());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(Long id1, Long id2) {
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        Set<Long> friendsUser1 = user1.getFriends();
        Set<Long> friendsUser2 = user2.getFriends();
        friendsUser1.add(id2);
        friendsUser2.add(id1);
        user1.setFriends(friendsUser1);
        user2.setFriends(friendsUser2);
        users.put(id1, user1);
        users.put(id2, user2);
    }

    @Override
    public void removeFriend(Long id1, Long id2) {
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        Set<Long> friendsUser1 = user1.getFriends();
        Set<Long> friendsUser2 = user2.getFriends();
        friendsUser1.remove(id2);
        friendsUser2.remove(id1);
        user1.setFriends(friendsUser1);
        user2.setFriends(friendsUser2);
        users.put(id1, user1);
        users.put(id2, user2);
    }

    @Override
    public Set<User> findFriends(Long id) {
        Set<Long> friendsId = users.get(id).getFriends();
        Set<User> friends = new HashSet<>();
        for (Long l : friendsId) {
            friends.add(users.get(l));
        }
        return friends;
    }

    private Long getNextId() {
        long curMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++curMaxId;
    }
}