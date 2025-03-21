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
import ru.yandex.practicum.filmorate.storage.FilmDirectorsStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

	private static final String FIND_ALL_QUERY = "SELECT f.*, " +
			"LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id, " +
			"LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name, " +
			"LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id, " +
			"LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name, " +
			"mr.name mpa_rating_name " +
			"FROM films f " +
			"LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id " +
			"LEFT JOIN films_genres fg ON fg.film_id = f.id " +
			"LEFT JOIN genres g ON g.id = fg.genre_id " +
			"LEFT JOIN film_directors fd ON fd.film_id = f.id " +
			"LEFT JOIN directors d ON d.id = fd.director_id " +
			"GROUP BY f.id";
	private static final String FIND_BY_ID_QUERY = "SELECT f.*, " +
			"LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id, " +
			"LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name, " +
			"LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id, " +
			"LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name, " +
			"mr.name mpa_rating_name " +
			"FROM films f " +
			"LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id " +
			"LEFT JOIN films_genres fg ON fg.film_id = f.id " +
			"LEFT JOIN genres g ON g.id = fg.genre_id " +
			"LEFT JOIN film_directors fd ON fd.film_id = f.id " +
			"LEFT JOIN directors d ON d.id = fd.director_id " +
			"WHERE f.id = ? " +
			"GROUP BY f.id";
	private static final String GET_COMMON_FILMS = "SELECT f.id, f.name, f.description, f.release_date, " +
			"f.duration, f.mpa_rating_id, m.name AS mpa_rating_name, " +
			"STRING_AGG(g.id, ',') AS genre_id, STRING_AGG(g.name, ',') AS genre_name, " +
			"LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id, " +
			"LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name " +
			"FROM films f " +
			"JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
			"JOIN films_likes l1 ON f.id = l1.film_id " +
			"JOIN films_likes l2 ON f.id = l2.film_id " +
			"LEFT JOIN films_genres fg ON f.id = fg.film_id " +
			"LEFT JOIN genres g ON fg.genre_id = g.id " +
			"LEFT JOIN film_directors fd ON fd.film_id = f.id " +
			"LEFT JOIN directors d ON d.id = fd.director_id " +
			"WHERE l1.user_id = ? AND l2.user_id = ? " +
			"GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, m.name " +
			"ORDER BY (SELECT COUNT(*) FROM films_likes fl WHERE fl.film_id = f.id) DESC";
	private static final String GET_DIRECTOR_FILMS_SORTED_BY_YEAR = """
			    SELECT f.*,
			    EXTRACT(YEAR FROM CAST(f.release_date AS DATE)) AS release_year,
			    mr.id AS mpa_rating_id,
			    mr.name AS mpa_rating_name,
			    LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
			    LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name,
			    LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id,
			    LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name
			    FROM films f
			    LEFT JOIN mpa_ratings mr ON f.mpa_rating_id = mr.id
			    LEFT JOIN films_genres fg ON fg.film_id = f.id
			    LEFT JOIN genres g ON g.id = fg.genre_id
			    LEFT JOIN film_directors fd ON fd.film_id = f.id
			    LEFT JOIN directors d ON d.id = fd.director_id
			    WHERE f.id IN (
			        SELECT film_id
			        FROM film_directors fd
			        WHERE fd.director_id = ?
			    )
			    GROUP BY f.id, release_year, mr.id, mr.name
			    ORDER BY release_year ASC
			""";
	private static final String GET_DIRECTOR_FILMS_SORTED_BY_LIKES = """
			    SELECT f.*,
			    fl.likes_count,
			    mr.id AS mpa_rating_id,
			    mr.name AS mpa_rating_name,
			    LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
			    LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name,
			    LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id,
			    LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name
			    FROM films f
			    LEFT JOIN (
			    	SELECT film_id, COUNT(user_id) AS likes_count
			    	FROM films_likes
			    	GROUP BY film_id
			    ) fl ON fl.film_id = f.id
			    LEFT JOIN mpa_ratings mr ON f.mpa_rating_id = mr.id
			    LEFT JOIN films_genres fg ON fg.film_id = f.id
			    LEFT JOIN genres g ON g.id = fg.genre_id
			    LEFT JOIN film_directors fd ON fd.film_id = f.id
			    LEFT JOIN directors d ON d.id = fd.director_id
			    WHERE f.id IN (
			    	SELECT film_id
			    	FROM film_directors fd
			    	WHERE fd.director_id = ?
			    )
			    GROUP BY f.id, fl.likes_count, mr.id, mr.name
			    ORDER BY fl.likes_count DESC
			""";
	private static final String SEARCH_BY_TITLE_QUERY =
			"SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
					"mr.name AS mpa_rating_name, COUNT(DISTINCT fl.user_id) AS like_count, " +
					"GROUP_CONCAT(DISTINCT fg.genre_id) AS genre_id, " +
					"GROUP_CONCAT(DISTINCT g.name) AS genre_name, " +
					"GROUP_CONCAT(DISTINCT fd.director_id) AS director_id, " +
					"GROUP_CONCAT(DISTINCT d.name) AS director_name " +
					"FROM films f " +
					"LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id " +
					"LEFT JOIN films_likes fl ON fl.film_id = f.id " +
					"LEFT JOIN films_genres fg ON fg.film_id = f.id " +
					"LEFT JOIN genres g ON g.id = fg.genre_id " +
					"LEFT JOIN film_directors fd ON fd.film_id = f.id " +
					"LEFT JOIN directors d ON d.id = fd.director_id " +
					"WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')) " +
					"GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, mr.name " +
					"ORDER BY like_count DESC";
	private static final String SEARCH_BY_DIRECTOR_QUERY =
			"SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
					"mr.name AS mpa_rating_name, COUNT(DISTINCT fl.user_id) AS like_count, " +
					"GROUP_CONCAT(DISTINCT fg.genre_id) AS genre_id, " +
					"GROUP_CONCAT(DISTINCT g.name) AS genre_name, " +
					"GROUP_CONCAT(DISTINCT fd.director_id) AS director_id, " +
					"GROUP_CONCAT(DISTINCT d.name) AS director_name " +
					"FROM films f " +
					"LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id " +
					"LEFT JOIN films_likes fl ON fl.film_id = f.id " +
					"LEFT JOIN films_genres fg ON fg.film_id = f.id " +
					"LEFT JOIN genres g ON g.id = fg.genre_id " +
					"JOIN film_directors fd ON fd.film_id = f.id " +
					"JOIN directors d ON d.id = fd.director_id " +
					"WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?, '%')) " +
					"GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, mr.name " +
					"ORDER BY like_count DESC";
	private static final String SEARCH_BY_TITLE_AND_DIRECTOR_QUERY = """
			    SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id,
			           mr.name AS mpa_rating_name,
			           COUNT(DISTINCT fl.user_id) AS like_count,
			           LISTAGG(fg.genre_id, ',') WITHIN GROUP (ORDER BY fg.genre_id) AS genre_id,
			           LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.name) AS genre_name,
			           LISTAGG(fd.director_id, ',') WITHIN GROUP (ORDER BY fd.director_id) AS director_id,
			           LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.name) AS director_name
			    FROM films f
			    LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id
			    LEFT JOIN films_likes fl ON fl.film_id = f.id
			    LEFT JOIN film_directors fd ON fd.film_id = f.id
			    LEFT JOIN directors d ON d.id = fd.director_id
			    LEFT JOIN films_genres fg ON fg.film_id = f.id
			    LEFT JOIN genres g ON g.id = fg.genre_id
			    WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?)
			    GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, mr.name
			    ORDER BY like_count DESC
			""";

	private static final String GET_RECOMMENDATION_FILMS = """
			SELECT f.*,
			LISTAGG(g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
			LISTAGG(g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name,
			LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id,
			LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name,
			mr.name mpa_rating_name
			FROM films f
			JOIN (
				SELECT DISTINCT fl.film_id
				FROM FILMS_LIKES fl
				WHERE fl.USER_ID IN (%s)
					AND NOT EXISTS (
						SELECT 1 FROM FILMS_LIKES fl2 WHERE fl2.FILM_ID = fl.FILM_ID AND fl2.USER_ID = ?
					)
			) rf ON rf.film_id = f.id
			LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id
			LEFT JOIN films_genres fg ON fg.film_id = f.id
			LEFT JOIN genres g ON g.id = fg.genre_id
			LEFT JOIN film_directors fd ON fd.film_id = f.id
			LEFT JOIN directors d ON d.id = fd.director_id
			GROUP BY f.id
			""";
	private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
	private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
	private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
	private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE id = ?";
	private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM films_genres WHERE film_id = ?";
	private static final String GET_ALL_FILM_GENRES_QUERY =
			"SELECT fg.film_id, g.id AS genre_id, g.name AS name FROM films_genres fg " +
					"LEFT JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id IN (%s)";

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

		if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
			filmDirectorsStorage.addFilmDirectors(film);
		}

		log.info("Фильм добавлен с ID: {}", film.getId());
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

		filmDirectorsStorage.updateFilmDirectors(newFilm);
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
		} else if (genreId == null) {
			return findMany(paramGetPopularQuery("WHERE YEAR (f.release_date) = ?"), year, count);
		} else {
			return findMany(paramGetPopularQuery("WHERE g.id = ? AND YEAR (f.release_date) = ?"), genreId, year, count);
		}
	}

	private String paramGetPopularQuery(String paramsString) {
		return String.format("""
				SELECT f.*,
				mr.name mpa_rating_name,
				allg.genre_id,
				allg.genre_name,
				LISTAGG(d.id, ',') WITHIN GROUP (ORDER BY d.id) AS director_id,
				LISTAGG(d.name, ',') WITHIN GROUP (ORDER BY d.id) AS director_name,
				COUNT(DISTINCT fl.user_id) AS like_count
				FROM films f
				LEFT JOIN mpa_ratings mr ON mr.id = f.mpa_rating_id
				LEFT JOIN films_genres fg ON fg.film_id = f.id
				LEFT JOIN genres g ON g.id = fg.genre_id
				LEFT JOIN films_likes fl ON fl.film_id = f.id
				LEFT JOIN film_directors fd ON fd.film_id = f.id
				LEFT JOIN directors d ON d.id = fd.director_id
				LEFT JOIN (
					SELECT fg.film_id,
						LISTAGG(DISTINCT g.id, ',') WITHIN GROUP (ORDER BY g.id) AS genre_id,
						LISTAGG(DISTINCT g.name, ',') WITHIN GROUP (ORDER BY g.id) AS genre_name
					FROM films_genres fg
					JOIN genres g ON g.id = fg.genre_id
					GROUP BY fg.film_id
				) allg ON allg.film_id = f.id
				%s
				GROUP BY f.id
				ORDER BY like_count DESC
				LIMIT ?""", paramsString);
	}

	public Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films) {
		log.info("Загрузка жанров для фильмов: {}", films);
		Map<Integer, List<Genre>> filmGenreMap = new HashMap<>();
		Collection<String> ids = films.stream()
				.map(film -> String.valueOf(film.getId()))
				.toList();

		log.debug("Запрос жанров для фильмов с ID: {}", ids);

		jdbc.query(String.format(GET_ALL_FILM_GENRES_QUERY, String.join(",", ids)), rs -> {
			Genre genre = Genre.builder()
					.id((long) rs.getInt("genre_id"))
					.name(rs.getString("name"))
					.build();

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
		Collection<Film> films = findMany(GET_COMMON_FILMS, userId, friendId);
		log.info("Общие фильмы найдены: {}", films);
		return films;
	}


	public Collection<Film> getDirectorFilmSortedByLike(Long directorId) {
		return findMany(GET_DIRECTOR_FILMS_SORTED_BY_LIKES, directorId);
	}

	public Collection<Film> getDirectorFilmSortedByYear(Long directorId) {
		return findMany(GET_DIRECTOR_FILMS_SORTED_BY_YEAR, directorId);
	}

	@Override
	public Collection<Film> searchFilms(String query, List<String> searchFields) {
		if (query == null) {
			throw new IllegalArgumentException("Параметр 'query' не может быть null");
		}
		String searchPattern = "%" + query + "%";

		if (searchFields.contains("title") && searchFields.contains("director")) {
			return findMany(SEARCH_BY_TITLE_AND_DIRECTOR_QUERY, searchPattern, searchPattern);
		} else if (searchFields.contains("director")) {
			return findMany(SEARCH_BY_DIRECTOR_QUERY, searchPattern);
		} else if (searchFields.contains("title")) {
			return findMany(SEARCH_BY_TITLE_QUERY, searchPattern);
		} else {
			throw new IllegalArgumentException("Некорректные поля поиска: " + searchFields);
		}
	}

	public Collection<Film> getRecommendationFilmsByUserId(Long userId, Set<Long> otherUserIds) {
		String query = String.format(
				GET_RECOMMENDATION_FILMS,
				String.join(",", otherUserIds.stream().map(String::valueOf).toList()));
		return findMany(query, userId);
	}
}

