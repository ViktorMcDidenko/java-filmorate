package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmTest {
    FilmController controller;
    Film film;
    int limitSymbols = FilmController.getDESCRIPTION_SYMBOLS();
    LocalDate limitDate = LocalDate.of(1895, 12, 28);

    @BeforeEach
    void beforeEach() {
        controller = new FilmController();
        film = new Film("Титаник",
                "Грустный фильм о кораблике.",
                LocalDate.of(1997, 8, 11),
                255);
    }

    @Test
    void create() {
        assertEquals(controller.findAll().size(), 0, "Список фильмов не пуст.");
        assertEquals(controller.create(film), film, "Ошибка при добавлении фильма.");
        assertEquals(controller.findAll().size(), 1, "Размер списка фильмов неверный.");
    }

    @Test
    void update() {
        controller.create(film);
        film.setName("Titanic");

        assertEquals(controller.findAll().size(), 1, "Размер списка фильмов до обновления неверный.");
        assertEquals(controller.update(film), film, "Ошибка при обновлении фильма.");
        assertEquals(controller.findAll().size(), 1, "Размер списка фильмов после обновления неверный.");
    }

    @Test
    void createFilmWithEmptyName() {
        film.setName(" ");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("Название фильма не может быть пустым.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void createFilmWithNullName() {
        film.setName(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("Название фильма не может быть пустым.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateFilmWithEmptyName() {
        controller.create(film);
        film.setName(" ");

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("Название фильма не может быть пустым.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateFilmWithNullName() {
        controller.create(film);
        film.setName(null);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("Название фильма не может быть пустым.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createFilmWithLimitLongDescription() {
        String longDescription = "a".repeat(limitSymbols);
        film.setDescription(longDescription);

        assertEquals(controller.create(film).getDescription().length(), limitSymbols);
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createFilmWithTooLongDescription() {
        String longDescription = "a".repeat(limitSymbols + 1);
        film.setDescription(longDescription);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("Максимальная длина описания в символах: " + limitSymbols + ". В вашем описании символов: "
                + (limitSymbols + 1), e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateFilmWithLimitLongDescription() {
        controller.create(film);
        String longDescription = "a".repeat(limitSymbols);
        film.setDescription(longDescription);

        assertEquals(controller.update(film).getDescription().length(), limitSymbols);
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateFilmWithTooLongDescription() {
        controller.create(film);
        String longDescription = "a".repeat(limitSymbols + 1);
        film.setDescription(longDescription);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("Максимальная длина описания в символах: " + limitSymbols + ". В вашем описании символов: "
                + (limitSymbols + 1), e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createLimitOldFilm() {
        film.setReleaseDate(limitDate);

        assertEquals(controller.create(film).getReleaseDate(), limitDate);
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void updateLimitOldFilm() {
        controller.create(film);
        film.setReleaseDate(limitDate);

        assertEquals(controller.update(film).getReleaseDate(), limitDate);
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createTooOldFilm() {
        film.setReleaseDate(limitDate.minusDays(1));

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("В нашем каталоге не может быть фильмов, снятых раньше 28.12.1895 года.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateTooOldFilm() {
        controller.create(film);
        film.setReleaseDate(limitDate.minusDays(1));

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("В нашем каталоге не может быть фильмов, снятых раньше 28.12.1895 года.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }

    @Test
    void createWithNonPositiveDuration() {
        film.setDuration(0);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.create(film));

        assertEquals("Продолжительность не может быть меньше 1 минуты.", e.getMessage());
        assertEquals(0, controller.findAll().size());
    }

    @Test
    void updateWithNonPositiveDuration() {
        controller.create(film);
        film.setDuration(0);

        ValidationException e = assertThrows(ValidationException.class,
                () -> controller.update(film));

        assertEquals("Продолжительность не может быть меньше 1 минуты.", e.getMessage());
        assertEquals(1, controller.findAll().size());
    }
}