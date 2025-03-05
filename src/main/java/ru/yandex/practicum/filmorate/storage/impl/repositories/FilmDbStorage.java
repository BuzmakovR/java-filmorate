package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

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
		if (genreId == null && year == null) return findMany(paramGetPopularQuery(""), count);
		else if (genreId != null && year == null) return findMany(paramGetPopularQuery(
				"WHERE g.id = ?"), genreId, count);
		else if (genreId == null && year != null) return findMany(
				paramGetPopularQuery("WHERE YEAR (f.release_date) = ?"), year, count);
		else if (genreId != null && year != null) return findMany(paramGetPopularQuery(
				"WHERE g.id = ? AND YEAR (f.release_date) = ?"), genreId, year, count);
		else throw new InternalServerException("Неверные входные параметры");
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
}
