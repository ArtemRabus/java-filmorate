create table IF NOT EXISTS MPA
(
    MPA_ID      INT primary key auto_increment,
    MPA_NAME    CHARACTER VARYING(10)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      LONG generated by default as identity primary key,
    name         varchar(50),
    FILM_NAME    CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(200) DEFAULT 'unknown',
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA_ID       INTEGER REFERENCES MPA(MPA_ID)
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID   INTEGER primary key,
    GENRE_NAME CHARACTER VARYING(50)
);

create table IF NOT EXISTS FILMS_GENRES
(
    GENRE_ID   INTEGER REFERENCES GENRES(GENRE_ID),
    FILM_ID    LONG REFERENCES FILMS(FILM_ID),
    constraint FILMS_GENRES_PK
        primary key (GENRE_ID, FILM_ID)
);

create table IF NOT EXISTS USERS
(
    USER_ID   LONG primary key auto_increment,
    USER_NAME CHARACTER VARYING(100) not null,
    LOGIN     CHARACTER VARYING(100),
    EMAIL     CHARACTER VARYING(100) not null,
    BIRTHDAY  DATE                   not null
);

create unique index USERS_EMAIL_UINDEX
    on USERS (EMAIL);

create unique index USERS_LOGIN_UINDEX
    on USERS (LOGIN);


create table IF NOT EXISTS LIKES
(
    FILM_ID LONG REFERENCES FILMS(FILM_ID),
    USER_ID LONG REFERENCES USERS(USER_ID),
    constraint LIKES_PK
        primary key (FILM_ID, USER_ID)
);

create table IF NOT EXISTS FRIENDS
(
    USER_ID   LONG references USERS(USER_ID),
    FRIEND_ID LONG references USERS(USER_ID),
    primary key (USER_ID, FRIEND_ID)
);

