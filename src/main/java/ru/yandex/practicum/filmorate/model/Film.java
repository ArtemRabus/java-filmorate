package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Data
public class Film {

    int id;

    String name;

    String description;

    Long releaseDate;

    Integer duration;

    public LocalDate getReleaseDate() {
        return Instant.ofEpochSecond(releaseDate).atOffset(ZoneOffset.UTC).toLocalDate();
    }

    public void setReleaseDate(LocalDate date) {
        releaseDate = date.toEpochSecond(LocalTime.ofSecondOfDay(0), ZoneOffset.UTC);
    }
}