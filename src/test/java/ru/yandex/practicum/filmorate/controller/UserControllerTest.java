package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private User user;
    private UserController controller;

    @BeforeEach
    protected void beforeEach() {
        controller = new UserController();
        user = new User();
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setBirthday(LocalDate.of(1946, 8, 20));
        user.setEmail("mail@mail.ru");
    }

    @Test
    @DisplayName("отрицательный id")
    protected void negativeIdTest() {
        user.setId(-1);
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Id не может быть отрицательным", ex.getMessage());
    }

    @Test
    @DisplayName("пустой логин")
    protected void loginNullTest() {
        user.setLogin(null);
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    @DisplayName("логин содержит пробел")
    protected void spaceLoginTest() {
        user.setLogin("John Wick");
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    @DisplayName("пустой адрес электронной почты")
    protected void emailNullTest() {
        user.setEmail(null);
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    @DisplayName("адрес электронной почты не содержит символ @")
    protected void emailSymbolTest() {
        user.setEmail("yandex.ru");
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    @DisplayName("пустое имя")
    protected void EmptyNameTest() throws ValidationException {
        user.setName("");
        controller.validateUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    @DisplayName("имя null")
    protected void nameNullTest() throws ValidationException {
        user.setName(null);
        controller.validateUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    @DisplayName("пустая дата рождения")
    protected void birthdayNullTest() {
        user.setBirthday(null);
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Дата рождения не может быть пустой или в будущем", ex.getMessage());
    }

    @Test
    @DisplayName("дата рождения в будущем")
    protected void birthdayInFutureTest() {
        user.setBirthday(LocalDate.of(2050, 1, 1));
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateUser(user));
        assertEquals("Дата рождения не может быть пустой или в будущем", ex.getMessage());
    }
}