package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService implements MainService<Film> {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate REFERENCE_DATE = LocalDate.of(1895,12,28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(long filmId) {
        check(filmId);
        return filmStorage.getById(filmId);
    }

    public Film create(Film film) throws ValidationException {
        validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) throws ValidationException {
        check(film.getId());
        validate(film);
        return filmStorage.update(film);
    }

    public void addLike(long filmId, long userId) {
        Film film = getById(filmId);
        User user = userStorage.getById(userId);
        film.getLike().add(user);
    }

    public void deleteLike(long filmId, long userId) {
        check(filmId);
        Film film = getById(filmId);
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        film.getLike().remove(user);
    }

    public List<Film> getListPopularFilm(long count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(film -> film.getLike().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public void validate(Film film) throws ValidationException {
        if (film.getId() < 0) {
            log.debug("id отрицателен");
            throw new ValidationException("Id не может быть отрицательным.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Пустое название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Описание больше 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(REFERENCE_DATE)) {
            log.debug("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.debug("Отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    public void check(long filmId) {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException(String.format("Фильм с id=%s не найден", filmId));
        }
    }
}
