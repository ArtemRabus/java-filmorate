package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("MPA_ID"),
                rs.getString("MPA_NAME"));
    }

    public Collection<Mpa> getAllMpa() {
        final String sqlQuery = "select * from MPA";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    public Optional<Mpa> getMpaById(long id) {
        final String sqlQuery = "select * from MPA where MPA_ID = ?";
        try {
            return
                    Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
