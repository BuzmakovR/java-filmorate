package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private static final String GET_COMMON_FILMS = "SELECT f.id, f.name, f.description, f.release_date, " +
			"f.duration, f.mpa_rating_id, m.name AS mpa_rating_name, " +
			"STRING_AGG(g.id, ',') AS genre_id, STRING_AGG(g.name, ',') AS genre_name " +
			"FROM films f " +
			"JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
			"JOIN films_likes l1 ON f.id = l1.film_id " +
			"JOIN films_likes l2 ON f.id = l2.film_id " +
			"LEFT JOIN films_genres fg ON f.id = fg.film_id " +
			"LEFT JOIN genres g ON fg.genre_id = g.id " +
			"WHERE l1.user_id = ? AND l2.user_id = ? " +
			"GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, m.name " +
			"ORDER BY (SELECT COUNT(*) FROM films_likes fl WHERE fl.film_id = f.id) DESC";


	private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
	private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";

	private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
	private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
	private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";

	public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
		super(jdbc, mapper);
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
		log.info("Добавление фильма: {}", film);
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
		log.info("Фильм добавлен с ID: {}", id);

		film.getGenres().forEach(genre -> {
			log.debug("Добавление жанра {} к фильму с ID: {}", genre, id);
			update(
					INSERT_FILM_GENRE_QUERY,
					film.getId(),
					genre.getId()
			);
		});
		log.info("Жанры успешно добавлены к фильму с ID: {}", id);

		return film;
	}

	@Override
	public Film update(Film newFilm) {
		log.info("Обновление фильма с ID: {}", newFilm.getId());
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
		log.info("Фильм с ID: {} обновлен", newFilm.getId());

		delete(DELETE_FILM_GENRES_QUERY, newFilm.getId());
		log.info("Старые жанры удалены для фильма с ID: {}", newFilm.getId());

		newFilm.getGenres().forEach(genre -> {
			log.debug("Обновление жанра {} к фильму с ID: {}", genre, newFilm.getId());
			update(
					INSERT_FILM_GENRE_QUERY,
					newFilm.getId(),
					genre.getId()
			);
		});
		log.info("Новые жанры добавлены к фильму с ID: {}", newFilm.getId());

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

	public Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films) {
		log.info("Загрузка жанров для фильмов: {}", films);
		final String getAllQuery = "SELECT fg.film_id, g.id AS genre_id, g.name AS name FROM films_genres fg " +
				"LEFT JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (%s)";

		Map<Integer, List<Genre>> filmGenreMap = new HashMap<>();
		Collection<String> ids = films.stream()
				.map(film -> String.valueOf(film.getId()))
				.toList();

		log.debug("Запрос жанров для фильмов с ID: {}", ids);

		jdbc.query(String.format(getAllQuery, String.join(",", ids)), rs -> {
			Genre genre = new Genre((long) rs.getInt("genre_id"),
					rs.getString("name"));

			Integer filmId = rs.getInt("film_id");

			filmGenreMap.putIfAbsent(filmId, new ArrayList<>());
			filmGenreMap.get(filmId).add(genre);
		});

		log.info("Загруженные жанры из БД: {}", filmGenreMap);
		return filmGenreMap;
	}

	@Override
	public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
		log.info("Запрос общих фильмов для пользователей с ID: {} и {}", userId, friendId);
		Collection<Film> films = jdbc.query(GET_COMMON_FILMS, mapper, userId, friendId);
		log.info("Общие фильмы найдены: {}", films);
		return films;
	}
}
