package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private Film film;
    private FilmService service;

    @BeforeEach
    protected void beforeEach() {
        service = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
        film = new Film();
        film.setName("John Wick");
        film.setDescription("the best killer");
        film.setReleaseDate(LocalDate.of(2015, 1, 1));
        film.setDuration(101);
    }

    @Test
    @DisplayName("отрицательный id")
    protected void validateIdTest() {
        film.setId(-1);
        Exception ex = assertThrows(ValidationException.class, () -> service.validate(film));
        assertEquals("Id не может быть отрицательным.", ex.getMessage());
    }

    @Test
    @DisplayName("название фильма - null")
    protected void nameNullTest() {
        film.setName(null);
        Exception ex = assertThrows(ValidationException.class, () -> service.validate(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("пустое название фильма")
    protected void nameBlankTest() {
        film.setName("");
        Exception ex = assertThrows(ValidationException.class, () -> service.validate(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("описание больше 200 символов")
    protected void descriptionMoreThan200Test() {
        film.setDescription("Джон Уик, на первый взгляд, - самый обычный среднестатистический американец, " +
                "который ведет спокойную мирную жизнь. Однако мало кто знает, что он был наёмным убийцей, " +
                "причём одним из лучших профессионалов в своём деле. После того, " +
                "как сынок главы бандитской группы со своими приятелями угоняет его любимый " +
                "«Мустанг» 1969 года выпуска, при этом убив его собаку Дейзи, которая была единственным напоминанием…");
        Exception ex = assertThrows(ValidationException.class, () -> service.validate(film));
        assertEquals("Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    @DisplayName("релиз раньше 20 декабря 1895 года")
    protected void dateReleaseTest() {
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> service.validate(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    @DisplayName("отрицательная продолжительность")
    protected void validateDurationTest() {
        film.setDuration(-10);
        Exception ex = assertThrows(ValidationException.class, () -> service.validate(film));
        assertEquals("Продолжительность фильма должна быть положительной", ex.getMessage());
    }
}