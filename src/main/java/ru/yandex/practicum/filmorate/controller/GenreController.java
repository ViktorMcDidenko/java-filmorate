package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.films.GenreDao;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreDao genreDao;

    @GetMapping
    public List<Genre> findAll() {
        return genreDao.getAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable int id) {
        return genreDao.getById(id);
    }
}