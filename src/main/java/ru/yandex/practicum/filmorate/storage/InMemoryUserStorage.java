package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @Override
    public User addUser(User user) {
        if(user.getLogin().contains(" ")) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        user.setId(userId++);
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("User with login {} was added successfully.", user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(!users.containsKey(user.getId())) {
            log.warn("The user was not found. Update failed.");
            throw new NotFoundException("There is no user with id: " + user.getId());
        }
        if(user.getLogin().contains(" ")) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        users.put(user.getId(), user);
        log.debug("User with login {} was updated successfully.", user.getLogin());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if(!users.containsKey(id)) {
            log.warn("The user was not found. Deletion failed.");
            throw new NotFoundException("There is no user with id: " + id);
        }
        users.remove(id);
        log.debug("User with id: {} was deleted successfully.", id);
    }

    @Override
    public List<User> findAllUsers() {
        log.debug("Current number of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById (int id) {
        if(!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id %d not found.", id));
        }
        return users.get(id);
    }
}