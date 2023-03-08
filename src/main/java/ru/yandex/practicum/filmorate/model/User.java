package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    int id;
    @NotNull
    @Email
    String email;
    @NotBlank
    String login;
    @Past
    LocalDate birthday;
    String name;

    public User(String email, String login, LocalDate birthday, String name) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
    }
}