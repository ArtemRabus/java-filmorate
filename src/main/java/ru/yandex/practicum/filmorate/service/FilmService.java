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

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return addFilmsGenresForList(films);
    }

    public Film getById(long filmId) throws SQLException {
        Optional<Film> filmById = filmDbStorage.getById(filmId);
        if (filmById.isPresent()) {
            Film film = filmById.get();
            film.setGenres(genreDbStorage.loadFilmGenre(film));
            return film;
        }
        throw new NotFoundException(String.format("Film with id = %s is not found", filmId));
    }

    public Film update(Film film) throws ValidationException, SQLException {
        validate(film);
        Optional<Film> res = filmDbStorage.update(film);
        if (res.isPresent()) {
            genreDbStorage.setFilmGenre(res.get());
            return res.get();
        }
        throw new NotFoundException(String.format("Film with id = %s is not found", film.getId()));
    }

    public void addLike(long filmId, long userId) {
        if (!likeDbStorage.addLike(filmId, userId)) {
            throw new NotFoundException("Error when adding a like");
        }
    }

    public void deleteLike(long filmId, long userId) throws SQLException {
        if (!likeDbStorage.deleteLike(filmId, userId)) {
            throw new NotFoundException("Error when deleting a like");
        }
    }

    public List<Film> getListPopularFilm(long count) {
        List<Film> films =  filmDbStorage.getListPopularFilm(count);
        return addFilmsGenresForList(films);
    }

    private List<Film> addFilmsGenresForList(List<Film> films) {
        return films.stream()
                .peek(film -> film.setGenres(genreDbStorage.loadFilmGenre(film)))
                .collect(Collectors.toList());
    }

    public void validate(Film film) throws ValidationException {
        if (film.getId() < 0) {
            log.debug("negative id");
            throw new ValidationException("Id cannot be negative");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("blank name");
            throw new ValidationException("The name cannot be blank");
        }
        if (film.getDescription().length() > 200) {
            log.debug("Description more than 200 characters");
            throw new ValidationException("The maximum length of the description is 200 characters");
        }
        if (film.getReleaseDate().isBefore(REFERENCE_DATE)) {
            log.debug("Release date earlier than December 28, 1895");
            throw new ValidationException("The release date should not be earlier than December 28, 1895");
        }
        if (film.getDuration() < 0) {
            log.debug("Negative duration of the film");
            throw new ValidationException("The duration of the film should be positive");
        }
    }
}
