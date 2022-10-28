package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements MainService<User> {
    private final UserStorage userDbStorage;
    private final FriendDbStorage friendDbStorage;

    public User create(User user) throws ValidationException {
        validate(user);
        return userDbStorage.create(user);
    }

    public User update(User user) throws ValidationException, SQLException {
        validate(user);
        Optional<User> res = userDbStorage.update(user);
        if (res.isPresent()) {
            return res.get();
        }
        throw new NotFoundException(String.format("Пользователь с id = %s не найден.", user.getId()));
    }

    public Collection<User> getAll() {
        try {
            return userDbStorage.getAll();
        } catch (Exception ex) {
            throw new NotFoundException("Ошибка получения списка пользоваелей");
        }
    }

    public User getById(long userId) throws SQLException {
        Optional<User> resUser = userDbStorage.getById(userId);
        if (resUser.isPresent()) {
            return resUser.get();
        }
        throw new NotFoundException(String.format("Пользователь с id = %s не найден.", userId));
    }

    public void addInFriend(long userId, long friendId) throws SQLException {
        try {
            friendDbStorage.addToFriends(userId, friendId);
        } catch (Exception ex) {
            throw new NotFoundException("Ошибка при добавлении друга");
        }
    }

    public List<User> getListFriends(long userId) throws SQLException {
        return friendDbStorage.getListFriends(userId);
    }

    public void deleteFromFriends(long userId, long friendId) throws SQLException {
        friendDbStorage.removeFromFriends(userId, friendId);
    }

    public List<User> getListCommonFriends(long user1, long user2) throws SQLException {
        return friendDbStorage.getListMutualFriends(user1, user2);
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
}
