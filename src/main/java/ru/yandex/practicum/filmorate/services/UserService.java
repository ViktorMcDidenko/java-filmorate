package ru.yandex.practicum.filmorate.services;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    public void addUser(User user) {
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
    }

    public boolean validateLogin(User user) {
        return user.getLogin().contains(" ");
    }

    public boolean validateUpdate(User user) {
        return users.containsKey(user.getId());
    }
}