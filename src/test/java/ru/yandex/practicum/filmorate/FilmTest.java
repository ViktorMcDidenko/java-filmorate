package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmTest {
    Film film = new Film();
    private Validator validator;
    private final FilmService service;

    @BeforeEach
    void beforeEach() {
        film.setName("Титаник");
        film.setDescription("Грустный фильм о кораблике.");
        film.setReleaseDate(LocalDate.of(1997, 8, 11));
        film.setDuration(255);
        film.setMpa(new Mpa(1, "G"));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void checkEmptyNameFilm() {
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(violations.size(), 1);
    }

    @Test
    void checkNullNameFilm() {
        film.setName(null);

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
        FilmController controller = new FilmController(service);
        film.setReleaseDate(LocalDate.of(1895, 12, 26));

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
        FilmController controller = new FilmController(service);
        controller.create(film);
        int wrongId = 89;
        film.setId(wrongId);

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> controller.update(film));

        assertEquals(String.format("Film with id %d not found.", wrongId), e.getMessage());
    }
}