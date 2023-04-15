package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public void addNewFriend(int id, int friendId) {
        storage.addNewFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        storage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(int id) {
        return storage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return storage.getCommonFriends(id, otherId);
    }

    public User addUser(User user) {
        return storage.addUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    public void deleteUser(int id) {
        storage.deleteUser(id);
    }

    public User getById(int id) {
        return storage.getById(id);
    }

    public List<User> findAll() {
        return storage.findAllUsers();
    }
}