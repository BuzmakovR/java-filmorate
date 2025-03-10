package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.DirectorRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository("directorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final Logger log = LoggerFactory.getLogger(DirectorDbStorage.class);
    private final JdbcTemplate jdbc;
    private final DirectorRowMapper mapper;

    @Override
    public Optional<Director> getById(Long id) {
        try {
            String findDirectorByIdQuery = "SELECT * FROM directors WHERE id = ?";
            Director director = jdbc.queryForObject(findDirectorByIdQuery, mapper, id);
            return Optional.ofNullable(director);
        } catch (DataAccessException e) {
            log.error("Error while getting director by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getAll() {
        String findAllDirectorsQuery = "SELECT * FROM directors";
        return jdbc.query(findAllDirectorsQuery, mapper);
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
        String updateDirectorQuery = "UPDATE directors SET name = ? WHERE id = ?";
        jdbc.update(updateDirectorQuery, newDirector.getName(), newDirector.getId());
        log.info("Updated director with id: {}", newDirector.getId());
        return newDirector;
    }

    @Override
    public void deleteById(Long id) {
        String deleteDirectorQuery = "DELETE FROM directors WHERE id = ?";
        jdbc.update(deleteDirectorQuery, id);
        log.info("Deleted director with id: {}", id);
    }

    @Override
    public Map<Long, Set<Director>> getAllFilmsDirectors() {
        String getAllFilmsDirectorsQuery = "SELECT f.id AS film_id, d.id AS director_id, d.name AS director_name " +
                "FROM films f " +
                "LEFT JOIN film_directors fd ON fd.film_id = f.id " +
                "LEFT JOIN directors d ON d.id = fd.director_id";
        Map<Long, Set<Director>> filmDirectors = new HashMap<>();

        jdbc.query(getAllFilmsDirectorsQuery, rs -> {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getObject("director_id", Long.class);
            String directorName = rs.getString("director_name");

            filmDirectors.computeIfAbsent(filmId, k -> new HashSet<>());
            if (directorId != null) {
                filmDirectors.get(filmId).add(Director.builder()
                        .id(directorId).name(directorName).build());
            }
        });

        return filmDirectors;
    }

    @Override
    public void saveDirectors(Film film) {
        Set<Director> filmDirectors = film.getDirectors();

        if (filmDirectors == null || filmDirectors.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES " +
                filmDirectors.stream()
                        .map(director -> "(?, ?)")
                        .collect(Collectors.joining(", "));

        List<Object> params = filmDirectors.stream()
                .flatMap(director -> Arrays.asList(film.getId(), director.getId()).stream())
                .collect(Collectors.toList());

        jdbc.update(sql, params.toArray());
        log.info("Saved directors for film with id: {}", film.getId());
    }

    @Override
    public void updateDirectors(Film film) {
        String deleteSql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbc.update(deleteSql, film.getId());
        log.info("Deleted directors for film with id: {}", film.getId());
        saveDirectors(film);
    }
}