package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmDirectorsStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT f.*, " +
            "LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id, " +
            "LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name, " +
            "mr.name mpa_rating_name, " +
            "FROM films f " +
            "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id " +
            "LEFT JOIN films_genres fg ON fg.film_id = f.id " +
            "LEFT JOIN genres g ON g.id = fg.genre_id " +
            "GROUP BY f.id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, " +
            "LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id, " +
            "LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name, " +
            "mr.name mpa_rating_name, " +
            "FROM films f " +
            "LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id " +
            "LEFT JOIN films_genres fg ON fg.film_id = f.id " +
            "LEFT JOIN genres g ON g.id = fg.genre_id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id";

    private static final String GET_DIRECTOR_FILMS_SORTED_BY_LIKES = """
                SELECT f.*,
                fl.likes_count,
                mr.id AS mpa_rating_id,
                mr.name AS mpa_rating_name,
                LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
                LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name
                FROM films f
                LEFT JOIN (
                SELECT film_id, COUNT(user_id) AS likes_count
                FROM films_likes
                GROUP BY film_id
                ) fl ON fl.film_id = f.id
                LEFT JOIN mpa_ratings mr ON f.mpa_rating_id = mr.id
                LEFT JOIN films_genres fg ON fg.film_id = f.id
                LEFT JOIN genres g ON g.id = fg.genre_id
                WHERE f.id IN (
                SELECT film_id
                FROM film_directors fd
                WHERE fd.director_id = ?
                )
                GROUP BY f.id, fl.likes_count, mr.id, mr.name
                ORDER BY fl.likes_count DESC
            """;

    private static final String GET_DIRECTOR_FILMS_SORTED_BY_YEAR = """
                SELECT f.*,
                EXTRACT(YEAR FROM CAST(f.release_date AS DATE)) AS release_year,
                mr.id AS mpa_rating_id,
                mr.name AS mpa_rating_name,
                LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
                LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name
                FROM films f
                LEFT JOIN mpa_ratings mr ON f.mpa_rating_id = mr.id
                LEFT JOIN films_genres fg ON fg.film_id = f.id
                LEFT JOIN genres g ON g.id = fg.genre_id
                WHERE f.id IN (
                    SELECT film_id
                    FROM film_directors fd
                    WHERE fd.director_id = ?
                )
                GROUP BY f.id, release_year, mr.id, mr.name
                ORDER BY release_year ASC
            """;

    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";

    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";

    private final FilmDirectorsStorage filmDirectorsStorage;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmDirectorsStorage filmDirectorsStorage) {
        super(jdbc, mapper);
        this.filmDirectorsStorage = filmDirectorsStorage;
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film get(Long id) {
        Optional<Film> optionalUser = findOne(FIND_BY_ID_QUERY, id);
        if (optionalUser.isEmpty()) throw new NotFoundException("Фильм с id = " + id + " не найден");
        return optionalUser.get();
    }

    @Override
    public Film add(Film film) {
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                Optional.ofNullable(film.getMpa())
                        .map(MpaRating::getId)
                        .orElse(null)
        );
        film.setId(id);

        film.getGenres().forEach(genre -> {
            update(
                    INSERT_FILM_GENRE_QUERY,
                    film.getId(),
                    genre.getId()
            );
        });
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        update(
                UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                Optional.ofNullable(newFilm.getMpa())
                        .map(MpaRating::getId)
                        .orElse(null),
                newFilm.getId()
        );
        delete(DELETE_FILM_GENRES_QUERY, newFilm.getId());

        newFilm.getGenres().forEach(genre -> {
            update(
                    INSERT_FILM_GENRE_QUERY,
                    newFilm.getId(),
                    genre.getId()
            );
        });

        if (newFilm.getDirectors() != null && !newFilm.getDirectors().isEmpty()) {
            filmDirectorsStorage.addFilmDirectors(newFilm);
        }
        return newFilm;
    }

    @Override
    public Film delete(Long id) {
        Film film = get(id);
        if (!delete(DELETE_FILM_QUERY, id)) {
            throw new InternalServerException("Не удалось удалить фильм");
        }
        return film;
    }

    @Override
    public Collection<Film> getPopular(Integer count, Long genreId, Integer year) {
        if (genreId == null && year == null) {
            return findMany(paramGetPopularQuery(""), count);
        } else if (genreId != null && year == null) {
            return findMany(paramGetPopularQuery("WHERE g.id = ?"), genreId, count);
        } else if (genreId == null && year != null) {
            return findMany(paramGetPopularQuery("WHERE YEAR (f.release_date) = ?"), year, count);
        } else {
            return findMany(paramGetPopularQuery("WHERE g.id = ? AND YEAR (f.release_date) = ?"), genreId, year, count);
        }
    }

    private String paramGetPopularQuery(String paramsString) {
        return String.format("""
                SELECT f.*,
                mr.name mpa_rating_name,
                LISTAGG(DISTINCT g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
                LISTAGG(DISTINCT g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name,
                COUNT(DISTINCT fl.user_id) AS like_count
                FROM films f
                LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id
                LEFT JOIN films_genres fg ON fg.film_id = f.id
                LEFT JOIN genres g ON g.id = fg.genre_id
                LEFT JOIN films_likes fl ON fl.film_id = f.id
                %s
                GROUP BY f.id
                ORDER BY like_count DESC
                LIMIT ?""", paramsString);
    }

    public Collection<Film> getDirectorFilmSortedByLike(Long directorId) {
        return findMany(GET_DIRECTOR_FILMS_SORTED_BY_LIKES, directorId);
    }

    public Collection<Film> getDirectorFilmSortedByYear(Long directorId) {
        return findMany(GET_DIRECTOR_FILMS_SORTED_BY_YEAR, directorId);
    }

}