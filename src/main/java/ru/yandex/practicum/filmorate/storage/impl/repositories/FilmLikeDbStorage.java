package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("filmLikeDbStorage")
public class FilmLikeDbStorage extends BaseRepository<FilmLike> implements FilmLikeStorage {

	private static final String FIND_LIKES_BY_ID = "SELECT * FROM films_likes WHERE film_id = ?";
	private static final String FIND_USER_ID_WITH_SAME_LIKES = """
			SELECT fl2.*
			FROM films_likes fl
			JOIN films_likes fl2 ON fl.film_id = fl2.film_id AND fl.user_id != fl2.user_id
			WHERE fl.user_id = ?""";
	private static final String DELETE_LIKES_BY_ID = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
	private static final String DELETE_ALL_LIKES_BY_ID = "DELETE FROM films_likes WHERE film_id = ?";
	private static final String INSERT_QUERY = "INSERT INTO films_likes(film_id, user_id) " +
			"SELECT CAST(? AS bigint) film_id, CAST(? AS bigint) user_id " +
			"FROM dual " +
			"WHERE NOT EXISTS (" +
			"SELECT 1 FROM films_likes " +
			"WHERE film_id = ? AND user_id = ?" +
			")";
	private static final String FIND_ALL_LIKES_QUERY = "SELECT * FROM films_likes";

	public FilmLikeDbStorage(JdbcTemplate jdbc, RowMapper<FilmLike> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Collection<Long> getFilmLikes(Long filmId) {
		return findMany(FIND_LIKES_BY_ID, filmId).stream()
				.map(FilmLike::getUserId)
				.toList();
	}

	@Override
	public Set<Long> getUsersWithSameLikes(Long userId) {
		return findMany(FIND_USER_ID_WITH_SAME_LIKES, userId).stream()
				.map(FilmLike::getUserId)
				.collect(Collectors.toSet());
	}

	@Override
	public void addFilmLike(Long filmId, Long userId) {
		updateWithoutCheck(
				INSERT_QUERY,
				filmId,
				userId,
				filmId,
				userId
		);
	}

	@Override
	public void deleteFilmLike(Long filmId, Long userId) {
		delete(
				DELETE_LIKES_BY_ID,
				filmId,
				userId
		);
	}

	@Override
	public Set<FilmLike> getAllLikes() {
		return Set.copyOf(findMany(FIND_ALL_LIKES_QUERY));
	}

	@Override
	public void deleteAllLikesForFilm(Long filmId) {
		delete(
				DELETE_ALL_LIKES_BY_ID,
				filmId
		);
	}
}
