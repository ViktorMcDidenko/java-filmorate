package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    UserService service = new UserService();
    private int userId = 1;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if(service.validateLogin(user)) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        user.setId(userId++);
        service.addUser(user);
        log.debug("User with login {} was added successfully.", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if(!service.validateUpdate(user)) {
            log.warn("The user was not found. Update failed.");
            throw new ValidationException("There is no user with id: " + user.getId());
        }
        if(service.validateLogin(user)) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        service.addUser(user);
        log.debug("User with login {} was updated successfully.", user.getLogin());
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Current number of users: {}", service.getUsers().size());
        return new ArrayList<>(service.getUsers().values());
    }
}