package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int filmId = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Release date is not valid.");
            throw new ValidationException("You cannot add pictures filmed before December 28, 1895.");
        }
        film.setId(filmId++);
        log.debug("Film with the title {} was added successfully.", film.getName());
        films.put(film.getId(), film);
        log.debug("Film with the title {} was added successfully.", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(!films.containsKey(film.getId())) {
            log.warn("The film was not found. Update failed.");
            throw new NotFoundException("There is no film with id: " + film.getId());
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Release date is not valid.");
            throw new ValidationException("You cannot add pictures filmed before December 28, 1895.");
        }
        films.put(film.getId(), film);
        log.debug("Film with the title {} was added successfully.", film.getName());
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if(!films.containsKey(id)) {
            log.warn("The film was not found. Deletion failed.");
            throw new NotFoundException("There is no film with id: " + id);
        }
        films.remove(id);
        log.debug("Film with id: {} was deleted successfully.", id);
    }

    @Override
    public List<Film> findAllFilms() {
        log.debug("Current number of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(int id) {
        if(!films.containsKey(id)) {
            throw new NotFoundException(String.format("Film with id %d not found.", id));
        }
        return films.get(id);
    }
}