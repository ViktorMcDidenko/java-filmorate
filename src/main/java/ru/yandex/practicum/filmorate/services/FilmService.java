package ru.yandex.practicum.filmorate.services;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FilmService {
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    public void addFilm(Film film) {
        films.put(film.getId(), film);
    }

    public boolean validateDate(Film film) {
        return film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
    }

    public boolean validateUpdate(Film film) {
        return films.containsKey(film.getId());
    }
}