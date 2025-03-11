package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.DirectorRowMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository("directorDbStorage")
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

    private static final String SELECT_DIRECTOR_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String SELECT_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO directors (id, name) VALUES (?, ?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String SELECT_ALL_FILM_DIRECTORS_QUERY = """
                    SELECT f.id AS film_id, d.id AS director_id, d.name AS director_name
                    FROM films f
                    LEFT JOIN film_directors fd ON fd.film_id = f.id
                    LEFT JOIN directors d ON d.id = fd.director_id
                """;

    public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director getById(Long id) {
        return findOne(SELECT_DIRECTOR_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID " + id + " не найден"));
    }

    @Override
    public List<Director> getAll() {
        return findMany(SELECT_ALL_DIRECTORS_QUERY);
    }

    @Override
    public Director create(Director director) {
        execUpdate(INSERT_DIRECTOR_QUERY, director.getId(), director.getName());
        log.info("Создан новый режиссёр с id: {}", director.getId());
        return director;
    }

    @Override
    public Director update(Director newDirector) {
        update(UPDATE_DIRECTOR_QUERY, newDirector.getName(), newDirector.getId());
        log.info("Обновлён режиссёр с ID: {}", newDirector.getId());
        return newDirector;
    }

    @Override
    public void deleteById(Long id) {
        if (!delete(DELETE_DIRECTOR_QUERY, id)) {
            log.warn("Режиссёр с ID {} не найден, удаление не выполнено", id);
            throw new NotFoundException("Режиссёр с ID " + id + " не найден");
        }
        log.info("Удалён режиссёр с ID: {}", id);
    }


    @Override
    public Map<Long, Set<Director>> getAllFilmsDirectors() {
        Map<Long, Set<Director>> filmDirectors = new HashMap<>();

        jdbc.query(SELECT_ALL_FILM_DIRECTORS_QUERY, rs -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getObject("director_id", Long.class);
            String directorName = rs.getString("director_name");

            filmDirectors.computeIfAbsent(filmId, k -> new HashSet<>());
            if (directorId != null) {
                filmDirectors.get(filmId).add(new Director(directorId, directorName));
            }
        });

        log.info("Загружены режиссёры для {} фильмов", filmDirectors.size());
        return filmDirectors;
    }
}
