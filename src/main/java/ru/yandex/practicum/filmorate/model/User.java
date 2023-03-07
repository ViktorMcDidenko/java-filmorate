package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    int id;
    String email;
    String login;
    LocalDate birthday;
    String name;

    public User(String email, String login, LocalDate birthday, String name) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
    }
}