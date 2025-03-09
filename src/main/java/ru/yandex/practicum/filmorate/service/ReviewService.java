package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

	@Qualifier("reviewDbStorage")
	private final ReviewStorage reviewStorage;

	@Qualifier("filmDbStorage")
	private final FilmStorage filmStorage;

	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	@Qualifier("feedDbStorage")
	private final FeedStorage feedStorage;

	public Review getReview(final Long reviewId) {
		return reviewStorage.get(reviewId);
	}

	public Collection<Review> getReviews(final Long filmId, int count) {
		return reviewStorage.getReviews(filmId, count);
	}

	public Review addReview(Review review) {
		filmStorage.get(review.getFilmId());
		userStorage.get(review.getUserId());
		Review request = reviewStorage.add(review);
		feedStorage.addEvent(request.getUserId(), request.getReviewId(), EventOperation.ADD, EventType.REVIEW);
		return request;
	}

	public Review updateReview(Review newReview) {
		if (newReview.getReviewId() == null) {
			throw new ValidationException("Id отзыва должен быть указан");
		}
		Review request = reviewStorage.update(newReview);
		feedStorage.addEvent(request.getUserId(), request.getReviewId(), EventOperation.UPDATE, EventType.REVIEW);
		return request;
	}

	public Review deleteReview(final Long reviewId) {
		Review review = getReview(reviewId);
		feedStorage.addEvent(review.getUserId(), reviewId, EventOperation.REMOVE, EventType.REVIEW);
		return reviewStorage.delete(reviewId);
	}

	public void addLike(ReviewLike reviewLike) {
		Review review = reviewStorage.get(reviewLike.getReviewId());
		userStorage.get(reviewLike.getUserId());
		reviewStorage.addLike(reviewLike);
		feedStorage.addEvent(reviewLike.getUserId(),review.getReviewId(), EventOperation.ADD, EventType.LIKE);
	}

	public void deleteLike(ReviewLike reviewLike) {
		Review review = reviewStorage.get(reviewLike.getReviewId());
		userStorage.get(reviewLike.getUserId());
		reviewStorage.deleteLike(reviewLike);
		feedStorage.addEvent(reviewLike.getUserId(), review.getReviewId(), EventOperation.REMOVE, EventType.LIKE );
	}
}
