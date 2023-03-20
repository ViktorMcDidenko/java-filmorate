package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    FilmService service = new FilmService();
    private int filmId = 1;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if(service.validateDate(film)) {
            log.warn("Release date is not valid.");
            throw new ValidationException("You cannot add pictures filmed before December 28, 1895.");
        }
        film.setId(filmId++);
        service.addFilm(film);
        log.debug("Film with the title {} was added successfully.", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if(!service.validateUpdate(film)) {
            log.warn("The film was not found. Update failed.");
            throw new ValidationException("There is no film with id: " + film.getId());
        }
        if(service.validateDate(film)) {
            log.warn("Release date is not valid.");
            throw new ValidationException("You cannot add pictures filmed before December 28, 1895.");
        }
        service.addFilm(film);
        log.debug("Film with the title {} was updated successfully.", film.getName());
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Current number of films: {}", service.getFilms().size());
        return new ArrayList<>(service.getFilms().values());
    }
}