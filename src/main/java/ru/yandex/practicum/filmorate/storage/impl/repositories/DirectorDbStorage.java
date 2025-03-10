package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.DirectorRowMapper;

import java.util.*;

@Repository("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final Logger log = LoggerFactory.getLogger(DirectorDbStorage.class);
    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    @Override
    public Optional<Director> getById(Long id) {
        try {
            String sql = "SELECT * FROM directors WHERE id = ?";
            return Optional.ofNullable(jdbc.queryForObject(sql, mapper, id));
        } catch (DataAccessException e) {
            log.warn("Режиссер с ID {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getAll() {
        return jdbc.query("SELECT * FROM directors", mapper);
    }

    @Override
    public Director create(Director director) {
        String insertDirectorQuery = "INSERT INTO directors (id, name) VALUES (?, ?)";
        jdbc.update(insertDirectorQuery, director.getId(), director.getName());
        log.info("Created new director with id: {}", director.getId());
        return director;
    }

    @Override
    public Director update(Director newDirector) {
        int rowsUpdated = jdbc.update("UPDATE directors SET name = ? WHERE id = ?",
                newDirector.getName(), newDirector.getId());

        if (rowsUpdated == 0) {
            log.warn("Режиссер с ID {} не найден, обновление не выполнено", newDirector.getId());
            throw new NotFoundException("Режиссер с ID " + newDirector.getId() + " не найден");
        }
        log.info("Обновлен режиссер с ID: {}", newDirector.getId());
        return newDirector;
    }

    @Override
    public void deleteById(Long id) {
        int rowsDeleted = jdbc.update("DELETE FROM directors WHERE id = ?", id);
        if (rowsDeleted == 0) {
            log.warn("Режиссер с ID {} не найден, удаление не выполнено", id);
            throw new NotFoundException("Режиссер с ID " + id + " не найден");
        }
        log.info("Удален режиссер с ID: {}", id);
    }

    @Override
    public Map<Long, Set<Director>> getAllFilmsDirectors() {
        String sql = """
                SELECT f.id AS film_id, d.id AS director_id, d.name AS director_name
                FROM films f
                LEFT JOIN film_directors fd ON fd.film_id = f.id
                LEFT JOIN directors d ON d.id = fd.director_id
            """;

        Map<Long, Set<Director>> filmDirectors = new HashMap<>();

        jdbc.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getObject("director_id", Long.class);
            String directorName = rs.getString("director_name");

            filmDirectors.computeIfAbsent(filmId, k -> new HashSet<>());
            if (directorId != null) {
                filmDirectors.get(filmId).add(new Director(directorId, directorName));
            }
        });

        log.info("Загружены режиссеры для {} фильмов", filmDirectors.size());
        return filmDirectors;
    }

    @Override
    public void saveDirectors(Film film) {
        Set<Director> filmDirectors = film.getDirectors();

        if (filmDirectors == null || filmDirectors.isEmpty()) {
            log.info("Для фильма с ID {} не указаны режиссеры, добавление пропущено", film.getId());
            return;
        }

        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        List<Object[]> batchParams = filmDirectors.stream()
                .map(director -> new Object[]{film.getId(), director.getId()})
                .toList();

        jdbc.batchUpdate(sql, batchParams);
        log.info("Добавлены {} режиссеров для фильма с ID: {}", batchParams.size(), film.getId());
    }

    @Override
    public void updateDirectors(Film film) {
        if (film.getId() == null) {
            log.warn("Попытка обновить режиссеров для фильма без ID");
            throw new IllegalArgumentException("ID фильма должен быть указан");
        }

        int rowsDeleted = jdbc.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        log.info("Удалены режиссеры ({} записей) для фильма с ID: {}", rowsDeleted, film.getId());

        saveDirectors(film);
    }
}