package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

	@Autowired
	@Qualifier("reviewDbStorage")
	private final ReviewStorage reviewStorage;

	@Autowired
	@Qualifier("filmDbStorage")
	private final FilmStorage filmStorage;

	@Autowired
	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	public Review getReview(final Long reviewId) {
		return reviewStorage.get(reviewId);
	}

	public Collection<Review> getReviews(final Long filmId, int count) {
		return reviewStorage.getReviews(filmId, count);
	}

	public Review addReview(Review review) {
		filmStorage.get(review.getFilmId());
		userStorage.get(review.getUserId());

		return reviewStorage.add(review);
	}

	public Review updateReview(Review newReview) {
		if (newReview.getReviewId() == null) {
			throw new ValidationException("Id отзыва должен быть указан");
		}
		return reviewStorage.update(newReview);
	}

	public Review deleteReview(final Long reviewId) {
		return reviewStorage.delete(reviewId);
	}

	public void addLike(ReviewLike reviewLike) {
		reviewStorage.get(reviewLike.getReviewId());
		userStorage.get(reviewLike.getUserId());
		reviewStorage.addLike(reviewLike);
	}

	public void deleteLike(ReviewLike reviewLike) {
		reviewStorage.get(reviewLike.getReviewId());
		userStorage.get(reviewLike.getUserId());
		reviewStorage.deleteLike(reviewLike);
	}
}
