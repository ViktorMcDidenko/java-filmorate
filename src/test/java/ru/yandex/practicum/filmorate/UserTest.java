package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserTest {
    User user;
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        user = new User("test@mail.ru",
                "test-login",
                LocalDate.of(1970, 12, 31),
                "Антон");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void checkNullEmailUser() {
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkBlankEmailUser() {
        user.setEmail(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkNoAtEmailUser() {
        user.setEmail("test-email.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkNullLoginUser() {
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkBlankLoginUser() {
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkLoginContainsBlanksUser() {
        UserController controller = new UserController(new UserService(new InMemoryUserStorage()));
        user.setLogin("test login with blanks");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(user));

        assertEquals("Your login should not contain blanks.", e.getMessage());
    }

    @Test
    void checkNullNameUser() {
        UserController controller = new UserController(new UserService(new InMemoryUserStorage()));
        user.setName(null);

        User savedUser = controller.create(user);

        assertEquals(savedUser.getLogin(), savedUser.getName());
    }

    @Test
    void checkBlankNameUser() {
        UserController controller = new UserController(new UserService(new InMemoryUserStorage()));
        user.setName("");

        User savedUser = controller.create(user);

        assertEquals(savedUser.getLogin(), savedUser.getName());
    }

    @Test
    void checkFutureBirthdayUser() {
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkUpdateWithNonExistentId() {
        UserController controller = new UserController(new UserService(new InMemoryUserStorage()));
        controller.create(user);
        int wrongId = 89;
        user.setId(wrongId);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> controller.update(user));

        assertEquals("There is no user with id: " + wrongId, e.getMessage());
    }
}