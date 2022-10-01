package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получить пользователей");
        return userService.getAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        log.info("Создать пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        log.info("Обновить пользователя");
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long userId) {
        log.info("Получить пользователя по id");
        return userService.getById(userId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getListFriends(@PathVariable("id") long userId) {
        log.info("Получить список друзей пользователя с id={}", userId);
        return userService.getListFriends(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<HttpStatus> addInFriends(@PathVariable("id") long userId,
                                                   @PathVariable("friendId") long friendId) {
        log.info("Добавить пользователя в друзья");
        userService.addInFriend(userId, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") long userId,
                                  @PathVariable("friendId") long friendId) {
        log.info("Удалить пользователя с друзей");
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getListCommonFriends(@PathVariable("id") long userId,
                                           @PathVariable("otherId") long otherId) {
        log.info("Получить список общих друзей");
        return userService.getListCommonFriends(userId, otherId);
    }
}