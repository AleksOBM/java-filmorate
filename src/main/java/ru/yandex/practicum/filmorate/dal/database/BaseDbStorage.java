package ru.yandex.practicum.filmorate.dal.database;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> rowMapper;

    protected <E> Set<E> findColumnByQuery(String query, Class<E> objectClass, Object... params) {
        try {
            return new HashSet<>(jdbc.queryForList(query, objectClass, params));
        } catch (EmptyResultDataAccessException ignored) {
            return HashSet.newHashSet(0);
        }
    }

    protected Optional<T> findOneByIdInTable(long id, String tableName) {
        return jdbc.query("SELECT * FROM " + tableName + " WHERE id = ?", rowMapper, id)
                .stream()
                .findFirst();
    }

    protected Collection<T> findAllInTable(String tableName) {
        return jdbc.query("SELECT * FROM " + tableName + " ORDER BY id", rowMapper)
                .stream()
                .toList();
    }

    protected boolean checkIdIsNotPresentInTable(long id, String tableName) {
        String sql = "SELECT ID FROM " + tableName + " WHERE ID = ?";
        return jdbc.queryForList(sql, Long.class, id).isEmpty();
    }

    protected long insertWithKeyHolder(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected void updateWithControl(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected List<T> findManyByQuery(String query, Object... params) {
        return jdbc.query(query, rowMapper, params);
    }
}
