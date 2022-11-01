package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Get a list of all films");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) throws SQLException {
        log.info("Get a film by id");
        return filmService.getById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        log.info("Create a film");
        return filmService.create(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, SQLException {
        log.info("Update the film");
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<HttpStatus> addLike(@PathVariable("id") long filmId,
                                              @PathVariable long userId) {
        log.info("Add a like");
        filmService.addLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<HttpStatus> deleteLike(@PathVariable("id") long filmId,
                                                 @PathVariable long userId) throws SQLException {
        log.info("Delete a like");
        filmService.deleteLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public Collection<Film> getListPopularFilm(@RequestParam(defaultValue = "10") int count) {
        log.info("Get a list of popular films");
        return filmService.getListPopularFilm(count);
    }
}