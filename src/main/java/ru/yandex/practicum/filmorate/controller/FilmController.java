package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Список фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм \"" +
                    film.getName() + "\" уже добавлен");
        } else {
            int id = film.getId();
            film.setId(++id);
            films.put(film.getId(), film);
            log.info("Фильм {} создан", film.getName());
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            createFilm(film);
        } else {
            films.put(film.getId(), film);
            log.info("Фильм {} обновлен", film.getName());
        }
        return film;
    }

    protected void validateFilm(Film film) throws ValidationException {
        if (film.getId() < 0) {
            log.debug("id отрицателен");
            throw new ValidationException("Id не может быть отрицательным.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("пустое название");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Описание больше 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.debug("Отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}