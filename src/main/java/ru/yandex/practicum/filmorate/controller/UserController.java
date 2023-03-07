package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @PostMapping
    public User create(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Необходимо указать электронную почту.");
        }
        if(!user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Неверный формат электронной почты.");
        }
        if(user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Придумайте логин.");
        }
        if(user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Логин не может содержать проблелы.");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        log.debug("Пользователь" + user.getLogin() + " добавлен.");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if(!users.containsKey(user.getId())) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Пользователь не найден.");
        }
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Необходимо указать электронную почту.");
        }
        if(!user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Неверный формат электронной почты.");
        }
        if(user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Придумайте логин.");
        }
        if(user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Логин не может содержать проблелы.");
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Пользователь" + user.getLogin() + "обновлён.");
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Текущее количество пользователей: " + users.size());
        return new ArrayList<>(users.values());
    }
}