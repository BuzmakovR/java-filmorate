package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.DbErrorException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BaseRepository<T> {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("[findOne] Не удалось получить данные: {}", e.getMessage());
            throw new DbErrorException("Не удалось получить данные: " + e.getMessage());
        }
    }

    protected List<T> findMany(String query, Object... params) {
        try {
            return jdbc.query(query, mapper, params);
        } catch (Exception e) {
            log.error("[findMany] Не удалось получить данные: {}", e.getMessage());
            throw new DbErrorException("Не удалось получить данные: " + e.getMessage());
        }
    }

    public boolean delete(String query, Object... params) {
        int rowsDeleted = execUpdate(query, params);
        return rowsDeleted > 0;
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
        } catch (Exception e) {
            log.error("[insert] Не удалось сохранить данные: {}", e.getMessage());
            throw new DbErrorException("Не удалось сохранить данные: " + e.getMessage());
        }
        return Optional.ofNullable(keyHolder.getKeyAs(Long.class))
                .orElseThrow(() -> new InternalServerException("Не удалось сохранить данные"));
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = execUpdate(query, params);
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не найдены данные для обновления");
        }
    }

    protected void updateWithoutCheck(String query, Object... params) {
        execUpdate(query, params);
    }

    protected Integer execUpdate(String query, Object... params) {
        try {
            return jdbc.update(query, params);
        } catch (Exception e) {
            log.error("[execUpdate] Не удалось обновить данные: {}", e.getMessage());
            throw new DbErrorException("Не удалось обновить данные: " + e.getMessage());
        }
    }
}
