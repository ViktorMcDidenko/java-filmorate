package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;

    public void addLike(int id, int userId) {
        storage.addLike(id, userId);
    }

    public void removeLike(int id, int userId) {
        storage.removeLike(id, userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return storage.getMostLikedFilms(count);
    }

    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return storage.updateFilm(film);
    }

    public void deleteFilm(int id) {
        storage.deleteFilm(id);
    }

    public List<Film> findAll() {
        return storage.findAllFilms();
    }

    public Film getById(int id) {
        return storage.getById(id);
    }
}