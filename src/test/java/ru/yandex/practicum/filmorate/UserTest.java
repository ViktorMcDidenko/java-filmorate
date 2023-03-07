package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserTest {
    UserController controller;
    User user;
    User savedUser;

    @BeforeEach
    void beforeEach() {
        controller = new UserController();
        user = new User("test@mail.ru",
                "test-login",
                LocalDate.of(1970, 12, 31),
                "Антон");
    }

    @Test
    void create() {
        assertEquals(controller.findAll().size(), 0, "Список пользователей не пуст.");
        assertEquals(controller.create(user), user, "Ошибка при добавлении пользователя.");
        assertEquals(controller.findAll().size(), 1, "Размер списка пользователей неверный.");
    }

    @Test
    void update() {
        controller.create(user);
        user.setName("Лёша");

        assertEquals(controller.findAll().size(), 1,
                "Размер списка пользователей до обновления неверный.");
        assertEquals(controller.update(user), user, "Ошибка при добавлении пользователя.");
        assertEquals(controller.findAll().size(), 1,
                "Размер списка пользователей после обновления неверный.");
    }

    @Test
    void createWithNullEmail() {
        user.setEmail(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Необходимо указать электронную почту.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void createWithBlankEmail() {
        user.setEmail(" ");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Необходимо указать электронную почту.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void createWithNoAtEmail() {
        user.setEmail("test-email.ru");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Неверный формат электронной почты.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateWithNullEmail() {
        controller.create(user);
        user.setEmail(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Необходимо указать электронную почту.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateWithBlankEmail() {
        controller.create(user);
        user.setEmail(" ");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Необходимо указать электронную почту.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateWithNoAtEmail() {
        controller.create(user);
        user.setEmail("test-email.ru");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Неверный формат электронной почты.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createWithNullLogin() {
        user.setLogin(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Придумайте логин.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void createWithBlankLogin() {
        user.setLogin(" ");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Придумайте логин.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void createWithLoginContainsBlanks() {
        user.setLogin("test login with blanks");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Логин не может содержать проблелы.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateWithNullLogin() {
        controller.create(user);
        user.setLogin(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Придумайте логин.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateWithBlankLogin() {
        controller.create(user);
        user.setLogin(" ");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Придумайте логин.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateWithLoginContainsBlanks() {
        controller.create(user);
        user.setLogin("test login with blanks");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Логин не может содержать проблелы.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createWithNullName() {
        user.setName(null);

        savedUser = controller.create(user);

        assertEquals(savedUser.getLogin(), savedUser.getName());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createWithBlankName() {
        user.setName("");

        savedUser = controller.create(user);

        assertEquals(savedUser.getLogin(), savedUser.getName());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateWithNullName() {
        controller.create(user);
        user.setName(null);

        savedUser = controller.update(user);

        assertEquals(savedUser.getLogin(), savedUser.getName());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateWithBlankName() {
        controller.create(user);
        user.setName("");

        savedUser = controller.update(user);

        assertEquals(savedUser.getLogin(), savedUser.getName());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createWithFutureBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Дата рождения не может быть в будущем.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateWithFutureBirthday() {
        controller.create(user);
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(user));

        assertEquals("Дата рождения не может быть в будущем.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }
}