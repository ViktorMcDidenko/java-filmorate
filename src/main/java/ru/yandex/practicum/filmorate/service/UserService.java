package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public void addNewFriend(int id, int friendId) {
        User user1 = storage.getById(id);
        User user2 = storage.getById(friendId);
        if(user1.equals(user2)) {
            throw new ValidationException("You can't add yourself to friends.");
        }
        user1.addFriend(friendId);
        user2.addFriend(id);
        log.debug("User {} and user {} are now friends.", user1.getName(), user2.getName());
    }

    public void deleteFriend(int id1, int id2) {
        storage.getById(id1).unfriend(id2);
        storage.getById(id2).unfriend(id1);
        log.debug("User with id {} and user with id {} are not friends anymore.", id1, id2);
    }

    public List<User> getFriends(int id) {
        Set<Integer> friends = storage.getById(id).getFriendsId();
        List<User> users = new ArrayList<>();
        for(int friend : friends) {
            users.add(storage.getById(friend));
        }
        log.debug("User with id {} has {} friends.", id, friends.size());
        return users;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> friends1 = storage.getById(id).getFriendsId();
        Set<Integer> friends2 = storage.getById(otherId).getFriendsId();
        List<User> users = new ArrayList<>();
        for(int friend : friends1) {
            if(friends2.contains(friend)) {
                users.add(storage.getById(friend));
            }
        }
        log.debug("User with id {} and user with id {} have {} common friends.", id, otherId, users.size());
        return users;
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