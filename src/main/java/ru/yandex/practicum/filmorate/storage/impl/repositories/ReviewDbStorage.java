package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository("reviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

	private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
	private static final String INSERT_QUERY = "INSERT INTO reviews(film_id, user_id, is_positive, content) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE reviews SET is_positive = ?, content = ? WHERE review_id = ?";
	private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = ?";

	public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Review get(Long id) {
		Optional<Review> optionalReview = findOne(FIND_BY_ID_QUERY, id);
		if (optionalReview.isEmpty()) throw new NotFoundException("Отзыв с id = " + id + " не найден");
		return optionalReview.get();
	}

	@Override
	public Review add(Review review) {
		long id = insert(
				INSERT_QUERY,
				review.getFilmId(),
				review.getUserId(),
				review.isPositive(),
				review.getContent()
		);
		review.setReviewId(id);
		return review;
	}

	@Override
	public Review update(Review review) {
		update(
				UPDATE_QUERY,
				review.isPositive(),
				review.getContent(),
				review.getReviewId()
		);
		return review;
	}

	@Override
	public Review delete(Long id) {
		Review review = get(id);
		if (!delete(DELETE_QUERY, id)) {
			throw new InternalServerException("Не удалось удалить отзыв");
		}
		return review;
	}

	@Override
	public Collection<Review> getReviewsForFilm(Long filmId, int count) {
		return List.of();
	}
}
