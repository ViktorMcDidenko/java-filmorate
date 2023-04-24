package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    List<Film> findAllFilms();

    Film getById(int id);

    void addLike(int id, int userId);

    void removeLike(int id, int userId);

    List<Film> getMostLikedFilms(int count);
}