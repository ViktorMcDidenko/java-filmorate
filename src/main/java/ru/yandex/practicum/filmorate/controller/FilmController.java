package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int filmId = 1;
    @Getter
    private static final int DESCRIPTION_SYMBOLS = 200;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if(film.getDescription().length() > DESCRIPTION_SYMBOLS) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Максимальная длина описания в символах: " + DESCRIPTION_SYMBOLS
                    + ". В вашем описании символов: " + film.getDescription().length());
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("В нашем каталоге не может быть фильмов, снятых раньше 28.12.1895 года.");
        }
        if(film.getDuration() < 1) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Продолжительность не может быть меньше 1 минуты.");
        }
        film.setId(filmId++);
        films.put(film.getId(), film);
        log.debug("Фильм '" + film.getName() + "' обновлён.");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if(!films.containsKey(film.getId())) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Фильм не найден.");
        }
        if(film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if(film.getDescription().length() > DESCRIPTION_SYMBOLS) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Максимальная длина описания в символах: " + DESCRIPTION_SYMBOLS
                    + ". В вашем описании символов: " + film.getDescription().length());
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("В нашем каталоге не может быть фильмов, снятых раньше 28.12.1895 года.");
        }
        if(film.getDuration() <= 0) {
            log.warn("Валидация не пройдена.");
            throw new ValidationException("Продолжительность не может быть меньше 1 минуты.");
        }
        films.put(film.getId(), film);
        log.debug("Фильм '" + film.getName() + "' обновлён.");
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: " + films.size());
        return new ArrayList<>(films.values());
    }
}