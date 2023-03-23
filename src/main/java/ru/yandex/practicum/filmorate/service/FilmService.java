package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;

    public void addLike(int id, int userId) {
        storage.getById(id).like(userId);
        log.debug("User with id {} liked the film with id {}.", userId, id);
    }

    public void removeLike(int id, int userId) {
        storage.getById(id).unlike(userId);
        log.debug("User with id {} unliked the film with id {}.", userId, id);
    }

    public List<Film> getMostLikedFilms(int count) {
        log.debug("Getting {} most liked films.", count);
        return storage.findAllFilms()
                .stream()
                .sorted((f1, f2) -> (f2.getLikes().size() - f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
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