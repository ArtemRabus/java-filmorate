package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class UserService implements MainService<User> {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) throws ValidationException {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User user) throws ValidationException {
        check(user.getId());
        validate(user);
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(long userId) {
        check(userId);
        return userStorage.getById(userId);
    }

    public void addInFriend(long userId, long friendId) {
        check(userId);
        check(friendId);

        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().add(getById(friendId));
        friend.getFriends().add(getById(userId));
    }

    public Set<User> getListFriends(long userId) {
        check(userId);
        User user = userStorage.getById(userId);
        return user.getFriends();
    }

    public void deleteFromFriends(long userId, long friendId) {
        check(userId);
        check(friendId);

        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    public List<User> getListCommonFriends(long user1, long user2) {
        check(user1);
        check(user2);

        List<User> commonFriends = new ArrayList<>(getById(user1).getFriends());
        List<User> fr = new ArrayList<>(getById(user2).getFriends());
        commonFriends.retainAll(fr);

        return commonFriends;
    }

    public void validate(User user) throws ValidationException {
        if (user.getId() < 0) {
            log.debug("отрицательный id");
            throw new ValidationException("Id не может быть отрицательным");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.debug("Неверный адрес электронной почты, пустой или не содержит @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.debug("Пустой логин или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Пустая дата рождения или в будущем");
            throw new ValidationException("Дата рождения не может быть пустой или в будущем");
        }
    }

    public void check(long userId) {
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
    }
}
