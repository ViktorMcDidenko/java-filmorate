package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmTest {
    Film film;
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        film = new Film("Титаник",
                "Грустный фильм о кораблике.",
                LocalDate.of(1997, 8, 11),
                255);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void checkEmptyNameFilm() {
        film.setTitle(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkNullNameFilm() {
        film.setTitle(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkFilmWithTooLongDescription() {
        String longDescription = "a".repeat(201);
        film.setDescription(longDescription);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkTooOldFilm() {
        FilmController controller = new FilmController(new FilmService(new InMemoryFilmStorage()));
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("You cannot add pictures filmed before December 28, 1895.", e.getMessage());
    }

    @Test
    void checkNonPositiveDurationFilm() {
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkUpdateWithNonExistentId() {
        FilmController controller = new FilmController(new FilmService(new InMemoryFilmStorage()));
        controller.create(film);
        int wrongId = 89;
        film.setFilmId(wrongId);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> controller.update(film));

        assertEquals("There is no film with id: " + wrongId, e.getMessage());
    }
}