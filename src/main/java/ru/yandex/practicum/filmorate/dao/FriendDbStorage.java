package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FriendDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User createUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("USER_NAME"),
                rs.getDate("BIRTHDAY").toLocalDate()
        );
    }

    public void addToFriends(long userId, long friendId) {
        final String sqlQuery = "insert into FRIENDS (USER_ID, FRIEND_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    public List<User> getListFriends(long id) throws SQLException {
        final String sqlQuery = "select U.USER_ID, U.USER_NAME, U.EMAIL, U.LOGIN, U.BIRTHDAY " +
                "from USERS U " +
                "where U.USER_ID in (select F.FRIEND_ID from FRIENDS F " +
                "where F.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::createUser, id);
    }

    public List<User> getListMutualFriends(long user1, long user2) {
        final String sqlQuery = "select U.USER_ID, U.USER_NAME, U.EMAIL, U.LOGIN, U.BIRTHDAY from USERS U " +
                "where U.USER_ID in (select F.FRIEND_ID from FRIENDS F " +
                "where F.USER_ID = ? " +
                "intersect " +
                "select F.FRIEND_ID from FRIENDS F " +
                "where F.USER_ID = ?)";
        return jdbcTemplate.query(sqlQuery, this::createUser, user1, user2);
    }

    public void removeFromFriends(long userId, long friendId) {
        final String sqlQuery = "delete from FRIENDS" +
                " where USER_ID = ? and FRIEND_ID = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }
}
