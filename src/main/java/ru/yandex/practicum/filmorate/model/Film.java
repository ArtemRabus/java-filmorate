package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class Film {
    private long id;

    @NotBlank
    private String name;

    @Size(min = 1, max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Min(1)
    private int duration;

    private Mpa mpa;

    private Set<Genre> genres;
}