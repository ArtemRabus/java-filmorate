package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    void getByIdTest() throws SQLException {
        Optional<Genre> genreOptional = genreDbStorage.getGenreById(4);
        Assertions.assertThat(genreOptional).isPresent()
                .hasValueSatisfying(genre -> Assertions.assertThat(genre)
                        .hasFieldOrPropertyWithValue("name", "Триллер"));
    }

    @Test
    void getAllTest() {
        assertEquals(6, genreDbStorage.getAllGenres().size());
    }

    @Test
    void setAndLoadGenreTest() throws ValidationException {
        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        Film film1 = new Film(1, "name", "description",
                LocalDate.of(2000, 5, 2), 30, new Mpa(1, "G"),
                genres);
        filmDbStorage.create(film1);

        genreDbStorage.setFilmGenre(film1);
        assertEquals(1, genreDbStorage.loadFilmGenre(film1).size());
    }

}