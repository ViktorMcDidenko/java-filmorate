package ru.yandex.practicum.filmorate.storage.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @Override
    public User addUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        user.setId(userId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("User with login {} was added successfully.", user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("The user was not found. Update failed.");
            throw new NotFoundException("There is no user with id: " + user.getId());
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Login validation failed.");
            throw new ValidationException("Your login should not contain blanks.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("User with login {} was updated successfully.", user.getLogin());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (!users.containsKey(id)) {
            log.warn("The user was not found. Deletion failed.");
            throw new NotFoundException("There is no user with id: " + id);
        }
        users.remove(id);
        log.debug("User with id: {} was deleted successfully.", id);
    }

    @Override
    public List<User> findAllUsers() {
        log.debug("Current number of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User with id %d not found.", id));
        }
        return users.get(id);
    }

    @Override
    public void addNewFriend(int id, int friendId) {
        if (id == friendId) {
            throw new ValidationException("You can't add yourself to friends.");
        }
        User user1 = getById(id);
        User user2 = getById(friendId);
        user1.addFriend(friendId);
        user2.addFriend(id);
        log.debug("User with id {} and user with id {} are friends now.", id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        getById(id).unfriend(friendId);
        getById(friendId).unfriend(id);
        log.debug("User with id {} and user with id {} are not friends anymore.", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        Set<Integer> friends = getById(id).getFriends();
        log.debug("User with id {} has {} friends.", id, friends.size());
        return friends.stream().map(this::getById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> friends1 = getById(id).getFriends();
        Set<Integer> friends2 = getById(otherId).getFriends();
        List<User> users = friends1.stream()
                .mapToInt(friend -> friend)
                .filter(friends2::contains)
                .mapToObj(this::getById)
                .collect(Collectors.toList());
        log.debug("User with id {} and user with id {} have {} common friends.", id, otherId, users.size());
        return users;
    }
}