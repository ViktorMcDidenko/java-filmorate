package ru.yandex.practicum.filmorate.storage.users;

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
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        nameValidation(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)",
                            new String[]{"id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        log.debug("User with login {} was added successfully.", user.getLogin());
        return getById(id);
    }

    @Override
    public User updateUser(User user) {
        if (getById(user.getId()) == null) {
            log.warn("The user was not found. Update failed.");
            throw new NotFoundException("There is no user with id: " + user.getId());
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        nameValidation(user);
        String sqlQuery = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        log.debug("User with login {} was updated successfully.", user.getLogin());
        return getById(user.getId());
    }

    @Override
    public void deleteUser(int id) {
        if (getById(id) == null) {
            log.warn("The user was not found. Deletion failed.");
            throw new NotFoundException("There is no user with id: " + id);
        }
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
        log.debug("User with id: {} was deleted successfully.", id);
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users", rowMapper());
        log.debug("Current number of users: {}", users.size());
        return users;
    }

    @Override
    public User getById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", rowMapper(), id);
        } catch (RuntimeException e) {
            throw new NotFoundException(String.format("User with id %d not found.", id));
        }
    }

    @Override
    public void addNewFriend(int id, int friendId) {
        if (id == friendId) {
            throw new ValidationException("You can't add yourself to friends.");
        }
        if ((getById(id) == null) || (getById(friendId) == null)) {
            log.warn("You can't add this user to friends. Check the ids.");
            throw new NotFoundException(String.format("Users with id %d and id %d can't be added to friends.",
                    id, friendId));
        }
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)", id, friendId);
        log.debug("User with id {} added user with id {} to friends.", id, friendId);
        int result = jdbcTemplate.update("UPDATE friends SET is_mutual = true WHERE user_id = ? AND friend_id = ?",
                friendId, id);
        if (result == 1) {
            jdbcTemplate.update("UPDATE friends SET is_mutual = true WHERE user_id = ? AND friend_id = ?",
                    id, friendId);
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if ((getById(id) == null) || (getById(friendId) == null)) {
            log.warn("You can't remove this user from friends. Check the ids.");
            throw new NotFoundException(String.format("Users with id %d and id %d can't be added to friends.",
                    id, friendId));
        }
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", id, friendId);
        jdbcTemplate.update("UPDATE friends SET is_mutual = false WHERE user_id = ? AND friend_id = ?",
                friendId, id);
        log.debug("User with id {} removed from friends user with id {}.", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        if ((getById(id) == null)) {
            log.warn("There is no user with id" + id);
            throw new NotFoundException(String.format("Users with id %d was not found.", id));
        }
        String sql = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        List<User> friends = jdbcTemplate.query(sql, rowMapper(), id);
        log.debug("User with id {} has {} friends.", id, friends.size());
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        if (getById(id) == null || getById(otherId) == null) {
            log.warn("You can't get common friends with this user. Check the ids.");
            throw new NotFoundException(String.format("Users with id %d and id %d don't have common friends. " +
                    "Check the ids.", id, otherId));
        }
        String sqlQuery = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friends WHERE user_id = ?)" +
                "AND id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        List<User> friends = jdbcTemplate.query(sqlQuery, rowMapper(), id, otherId);
        log.debug("User with id {} and user with id {} have {} common friends.", id, otherId, friends.size());
        return friends;
    }

    private RowMapper<User> rowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }

    private void nameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}