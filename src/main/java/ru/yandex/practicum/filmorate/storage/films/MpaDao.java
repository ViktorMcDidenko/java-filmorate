package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public Mpa getById(int id) {
        List<Mpa> mpa = jdbcTemplate.query("SELECT * FROM mpa WHERE id = ?",
                (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name")), id);
        if (mpa.size() != 1) {
            throw new NotFoundException(String.format("Film with id %d not found.", id));
        }
        return mpa.get(0);
    }

    public List<Mpa> getAll() {
        return jdbcTemplate.query("SELECT * FROM mpa", (rs, rowNum) -> new Mpa(rs.getInt("id"),
                rs.getString("name")));
    }
}