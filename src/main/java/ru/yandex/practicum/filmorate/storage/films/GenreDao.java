package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public Genre getById(int id) {
        List<Genre> genre = jdbcTemplate.query("SELECT * FROM genre WHERE id = ?",
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), id);
        if (genre.size() != 1) {
            throw new NotFoundException(String.format("Film with id %d not found.", id));
        }
        return genre.get(0);
    }

    public List<Genre> getAll() {
        return jdbcTemplate.query("SELECT * FROM genre", (rs, rowNum) -> new Genre(rs.getInt("id"),
                rs.getString("name")));
    }

    Set<Genre> addGenresToFilm(int id) {
        String sql = "SELECT g.id FROM genre AS g JOIN film_genre AS fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> getById(rs.getInt("id")), id);
        return new HashSet<>(genres);
    }

    void addGenresFromFilm(int id, Set<Genre> genres) {
        if (!genres.isEmpty()) {
            genres.forEach(genre -> jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", id, genre.getId()));
        }
    }
}