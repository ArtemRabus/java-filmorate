package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService implements MainService<Film> {
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private static final LocalDate REFERENCE_DATE = LocalDate.of(1895,12,28);

    public Film create(Film film) throws ValidationException {
        validate(film);
        Film newFilm = filmDbStorage.create(film);
        genreDbStorage.setFilmGenre(newFilm);
        return newFilm;
    }

    public Collection<Film> getAll() {
        List<Film> films = filmDbStorage.getAll();
        for (Film film : films) {
            Set<Genre> genre = genreDbStorage.loadFilmGenre(film);
            film.setGenres(genre);
        }
        return films;
    }

    public Film getById(long filmId) throws SQLException {
        Optional<Film> filmById = filmDbStorage.getById(filmId);
        if (filmById.isPresent()) {
            Film film = filmById.get();
            film.setGenres(genreDbStorage.loadFilmGenre(film));
            return film;
        }
        throw new NotFoundException(String.format("Фильм с id = %s не найден.", filmId));
    }

    public Film update(Film film) throws ValidationException, SQLException {
        validate(film);
        Optional<Film> res = filmDbStorage.update(film);
        if (res.isPresent()) {
            genreDbStorage.setFilmGenre(res.get());
            return res.get();
        }
        throw new NotFoundException(String.format("Фильм с id = %s не найден.", film.getId()));
    }

    public void addLike(long filmId, long userId) {
        if (!likeDbStorage.addLike(filmId, userId)) {
            throw new NotFoundException("Ошибка при добавлении лайка.");
        }
    }

    public void deleteLike(long filmId, long userId) throws SQLException {
        if (!likeDbStorage.deleteLike(filmId, userId)) {
            throw new NotFoundException("Ошибка при удалении лайка.");
        }
    }

    public List<Film> getListPopularFilm(long count) {
        List<Film> films =  filmDbStorage.getListPopularFilm(count);
        for (Film film : films) {
            Set<Genre> genre = genreDbStorage.loadFilmGenre(film);
            film.setGenres(genre);
        }
        return films;
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
}
