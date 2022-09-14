package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {FilmController.class})
@ExtendWith(SpringExtension.class)
class FilmControllerTest {
    private Film film;
    private FilmController controller;

    @BeforeEach
    protected void beforeEach() {
        controller = new FilmController();
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
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateFilm(film));
        assertEquals("Id не может быть отрицательным.", ex.getMessage());
    }

    @Test
    @DisplayName("название фильма - null")
    protected void nameNullTest() {
        film.setName(null);
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateFilm(film));
        assertEquals("название не может быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("пустое название фильма")
    protected void nameBlankTest() {
        film.setName("");
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateFilm(film));
        assertEquals("название не может быть пустым", ex.getMessage());
    }

    @Test
    @DisplayName("описание больше 200 символов")
    protected void descriptionMoreThan200Test() {
        film.setDescription("Джон Уик, на первый взгляд, - самый обычный среднестатистический американец, " +
                "который ведет спокойную мирную жизнь. Однако мало кто знает, что он был наёмным убийцей, " +
                "причём одним из лучших профессионалов в своём деле. После того, " +
                "как сынок главы бандитской группы со своими приятелями угоняет его любимый " +
                "«Мустанг» 1969 года выпуска, при этом убив его собаку Дейзи, которая была единственным напоминанием…");
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateFilm(film));
        assertEquals("Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    @DisplayName("релиз раньше 20 декабря 1895 года")
    protected void dateReleaseTest() {
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        Exception exception = assertThrows(ValidationException.class, () -> controller.validateFilm(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    @DisplayName("отрицательная продолжительность")
    protected void validateDurationTest() {
        film.setDuration(-10);
        Exception ex = assertThrows(ValidationException.class, () -> controller.validateFilm(film));
        assertEquals("Продолжительность фильма должна быть положительной", ex.getMessage());
    }

    @Test
    void testCreateFilm() throws ValidationException {
        FilmController filmController = new FilmController();

        Film film = new Film();
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        Film actualCreateFilmResult = filmController.createFilm(film);
        assertSame(film, actualCreateFilmResult);
        assertEquals(2, actualCreateFilmResult.getId());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void testCreateFilm2() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        filmController.createFilm(film);
        verify(film, atLeast(1)).getId();
        verify(film).getDuration();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film, atLeast(1)).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void testCreateFilm3() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenThrow(new ValidationException("An error occurred"));
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        verify(film).getId();
        verify(film).getDuration();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testCreateFilm4() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(-1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        verify(film).getId();
        verify(film).getDuration();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testCreateFilm5() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofYearDay(1, 1));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        verify(film).getId();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testCreateFilm6() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(-1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        verify(film).getId();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testCreateFilm7() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn(null);
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        verify(film).getId();
        verify(film).getName();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testCreateFilm8() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        verify(film).getId();
        verify(film, atLeast(1)).getName();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testUpdateFilm() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        Film actualUpdateFilmResult = filmController.updateFilm(film);
        assertSame(film, actualUpdateFilmResult);
        assertEquals(2, actualUpdateFilmResult.getId());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void testUpdateFilm2() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        filmController.updateFilm(film);
        verify(film, atLeast(1)).getId();
        verify(film, atLeast(1)).getDuration();
        verify(film, atLeast(1)).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film, atLeast(1)).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film, atLeast(1)).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void testUpdateFilm3() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenThrow(new ValidationException("An error occurred"));
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        verify(film).getId();
        verify(film).getDuration();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testUpdateFilm4() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(-1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        verify(film).getId();
        verify(film).getDuration();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testUpdateFilm5() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofYearDay(1, 1));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        verify(film).getId();
        verify(film).getDescription();
        verify(film, atLeast(1)).getName();
        verify(film).getReleaseDate();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testUpdateFilm6() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(-1);
        when(film.getName()).thenReturn("Name");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        verify(film).getId();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testUpdateFilm7() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn(null);
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        verify(film).getId();
        verify(film).getName();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testUpdateFilm8() throws ValidationException {
        FilmController filmController = new FilmController();
        Film film = mock(Film.class);
        when(film.getDuration()).thenReturn(1);
        when(film.getReleaseDate()).thenReturn(LocalDate.ofEpochDay(1L));
        when(film.getDescription()).thenReturn("The characteristics of someone or something");
        when(film.getId()).thenReturn(1);
        when(film.getName()).thenReturn("");
        doNothing().when(film).setDescription(any());
        doNothing().when(film).setDuration(any());
        doNothing().when(film).setId(anyInt());
        doNothing().when(film).setName(any());
        doNothing().when(film).setReleaseDate(any());
        film.setDescription("The characteristics of someone or something");
        film.setDuration(1);
        film.setId(1);
        film.setName("Name");
        film.setReleaseDate(LocalDate.ofEpochDay(1L));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        verify(film).getId();
        verify(film, atLeast(1)).getName();
        verify(film).setDescription(any());
        verify(film).setDuration(any());
        verify(film).setId(anyInt());
        verify(film).setName(any());
        verify(film).setReleaseDate(any());
    }

    @Test
    void testGetFilms() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/films");
        MockMvcBuilders.standaloneSetup(controller)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testGetFilms2() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/films", "Uri Variables");
        MockMvcBuilders.standaloneSetup(controller)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }
}