package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Primary
@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final UserDbStorage userStorage;
    private static final String SQL_TO_FIND_FILMS = "SELECT f.id, f.title, f.description, f.release_date, f.duration," +
            " f.mpa AS mpa_id, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa = m.id";

    @Override
    public Film addFilm(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Release date is not valid.");
            throw new ValidationException("You cannot add pictures filmed before December 28, 1895.");
        }
        String sqlQuery = "INSERT INTO films (title, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        log.debug("Film with the title {} was added successfully.", film.getName());
        genreDao.addGenresFromFilm(id, film.getGenres());
        return getById(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if(getById(film.getId()) == null) {
            log.warn("The film was not found. Update failed.");
            throw new NotFoundException("There is no film with id: " + film.getId());
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Release date is not valid.");
            throw new ValidationException("You cannot add pictures filmed before December 28, 1895.");
        }
        String sqlQuery = "UPDATE films SET title = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        genreDao.addGenresFromFilm(film.getId(), film.getGenres());
        log.debug("Film with the title {} was updated successfully.", film.getName());
        return getById(film.getId());
    }

    @Override
    public void deleteFilm(int id) {
        if(getById(id) == null) {
            log.warn("The film was not found. Deletion failed.");
            throw new NotFoundException("There is no film with id: " + id);
        }
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
        log.debug("Film with id: {} was deleted successfully.", id);
    }

    @Override
    public List<Film> findAllFilms() {
        List<Film> films = jdbcTemplate.query(SQL_TO_FIND_FILMS, rowMapper());
        log.debug("Current number of films: {}", films.size());
        return films;
    }

    @Override
    public Film getById(int id) {
        String sql = SQL_TO_FIND_FILMS + " WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, rowMapper(), id);
        if (films.size() != 1) {
            throw new NotFoundException(String.format("Film with id %d not found.", id));
        }
        return films.get(0);
    }

    @Override
    public void addLike(int id, int userId) {
        if(userStorage.getById(userId) == null) {
            log.warn("The user was not found.");
            throw new NotFoundException("There is no user with id: " + userId);
        }
        jdbcTemplate.update("INSERT INTO likes VALUES (?, ?)", id, userId);
        log.debug("User with id {} liked the film with id {}.", userId, id);
    }

    @Override
    public void removeLike(int id, int userId) {
        if(userStorage.getById(userId) == null) {
            log.warn("The user was not found.");
            throw new NotFoundException("There is no user with id: " + userId);
        }
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", id, userId);
        log.debug("User with id {} unliked the film with id {}.", userId, id);
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) FROM films AS f LEFT OUTER JOIN likes AS l ON l.film_id = f.id " +
                "GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, rowMapper(), count);
    }

    private RowMapper<Film> rowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("title"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(mpaDao.getById(rs.getInt("mpa")));
            film.setGenres(genreDao.addGenresToFilm(film.getId()));
            return film;
        };
    }
}