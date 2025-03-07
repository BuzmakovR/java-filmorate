package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository("reviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

	@Autowired
	private final ReviewLikeDbStorage reviewLikeDbStorage;

	private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
	private static final String INSERT_QUERY = "INSERT INTO reviews(film_id, user_id, is_positive, content) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE reviews SET is_positive = ?, content = ? WHERE review_id = ?";
	private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";

	public ReviewDbStorage(JdbcTemplate jdbc,
						   RowMapper<Review> mapper,
						   ReviewLikeDbStorage reviewLikeDbStorage) {
		super(jdbc, mapper);
		this.reviewLikeDbStorage = reviewLikeDbStorage;
	}

	@Override
	public Review get(Long id) {
		Optional<Review> optionalReview = findOne(FIND_BY_ID_QUERY, id);
		if (optionalReview.isEmpty()) throw new NotFoundException("Отзыв с id = " + id + " не найден");

		Review review = optionalReview.get();
		reviewLikeDbStorage.getReviewLikes(review.getReviewId())
				.forEach(review::addReviewLike);
		return review;
	}

	@Override
	public Review add(Review review) {
		long id = insert(
				INSERT_QUERY,
				review.getFilmId(),
				review.getUserId(),
				review.getIsPositive(),
				review.getContent()
		);
		review.setReviewId(id);
		return review;
	}

	@Override
	public Review update(Review review) {
		update(
				UPDATE_QUERY,
				review.getIsPositive(),
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
	public Collection<Review> getReviews(Long filmId, Integer count) {
		count = count == null ? 10 : count;
		Collection<Review> reviews;
		String templateQuery = "SELECT * FROM reviews WHERE 1=1 %s LIMIT ?";
		if (filmId == null) {
			reviews = findMany(String.format(templateQuery, ""), count);
		} else {
			reviews = findMany(String.format(templateQuery, " AND film_id = ?"), filmId, count);
		}
		reviews.forEach(review -> {
			reviewLikeDbStorage.getReviewLikes(review.getReviewId())
					.forEach(review::addReviewLike);
		});
		return reviews;
	}

	@Override
	public void addLike(ReviewLike reviewLike) {
		reviewLikeDbStorage.addLike(reviewLike);
	}

	@Override
	public void deleteLike(ReviewLike reviewLike) {
		reviewLikeDbStorage.deleteLike(reviewLike);
	}
}
