package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDateTime birthday;

    //Конструктор для тестов
    public User(String email, String login, String name, LocalDateTime birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}