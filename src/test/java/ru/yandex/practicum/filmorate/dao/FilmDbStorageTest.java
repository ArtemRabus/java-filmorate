package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    @Test
    @Order(10)
    void createAndGetAllFilmTest() throws ValidationException {
        Film film = new Film(1, "name", "description",
                LocalDate.of(2020, 1, 1), 90, new Mpa(4, "R"),
                new HashSet<>());

        filmDbStorage.create(film);
        assertEquals(2, filmDbStorage.getAll().size());
    }

    @Test
    @Order(20)
    void getByIdTest() throws SQLException {
        Optional<Film> filmOptional = filmDbStorage.getById(2);
        assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("name", "name"));
    }

    @Test
    @Order(30)
    void getPopularTest() throws ValidationException {
        Film film1 = new Film(2, "film", "description",
                LocalDate.of(2021, 1, 1), 30, new Mpa(2, "PG"),
                new HashSet<>());
        filmDbStorage.create(film1);

        List<Film> film = filmDbStorage.getListPopularFilm(1);
        assertEquals(1, film.size());
    }

    @Test
    @Order(40)
    void updateTest() throws ValidationException, SQLException {
        Film film1 = new Film(1, "film", "description2",
                LocalDate.of(2018, 1, 1), 30, new Mpa(1, "G"),
                new HashSet<>());

        filmDbStorage.update(film1);
        Optional<Film> filmOptional = filmDbStorage.getById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name",
                                "film"));
    }
}