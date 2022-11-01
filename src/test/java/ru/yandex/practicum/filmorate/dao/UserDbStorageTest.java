package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Order(10)
    @Test
    void createAndGetAllUserTest() {
        User user = new User(1, "test1@email","test_login1", "test_name1",
                LocalDate.of(2000,1,1));

        userDbStorage.create(user);
        assertEquals(6, userDbStorage.getAll().size());
    }

    @Order(20)
    @Test
    void testGetById() throws SQLException {
        Optional<User> userOptional = userDbStorage.getById(1);
        org.assertj.core.api.Assertions.assertThat(userOptional).isPresent()
                .hasValueSatisfying(user -> org.assertj.core.api.Assertions.assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "name3"));
    }

    @Order(30)
    @Test
    void updateTest() throws SQLException {
        User user = new User(1, "test6@ya.ru","login6", "user6",
                LocalDate.of(2001,1,1));

        userDbStorage.update(user);
        Optional<User> userOptional = userDbStorage.getById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("name",
                                "user6"));
    }
}