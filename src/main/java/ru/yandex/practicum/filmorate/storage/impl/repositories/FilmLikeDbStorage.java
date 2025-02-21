package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.util.Collection;

@Repository("filmLikeDbStorage")
public class FilmLikeDbStorage extends BaseRepository<FilmLike> implements FilmLikeStorage {

	private static final String FIND_LIKES_BY_ID = "SELECT * FROM films_likes WHERE film_id = ?";
	private static final String DELETE_LIKES_BY_ID = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
	private static final String INSERT_QUERY = "INSERT INTO films_likes(film_id, user_id) VALUES(?, ?)";

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
	public void addFilmLike(Long filmId, Long userId) {
		update(
				INSERT_QUERY,
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

}
