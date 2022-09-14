package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
public class User {

    int id;

    @Email
    String email;

    String login;

    String name;

    LocalDate birthday;
}