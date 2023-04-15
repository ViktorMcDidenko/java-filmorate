package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @NotNull
    @Email
    private String email;
    @NotBlank
    private String login;
    @Past
    private LocalDate birthday;
    private String name;
    private Set<Integer> friends = new HashSet<>();

    public void addFriend(int id) {
        friends.add(id);
    }

    public void unfriend(int id) {
        if(!friends.contains(id)) {
            throw new NotFoundException(String.format("User %s is not a friend of the user with id %d.",
                    getName(), id));
        }
        friends.remove(id);
    }
}