package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

public abstract class ReviewServiceTests {

	@Autowired
	protected ReviewService reviewService;

	@Autowired
	protected ReviewStorage reviewStorage;

	@Autowired
	protected FilmStorage filmStorage;

	@Autowired
	protected FilmLikeStorage filmLikeStorage;

	@Autowired
	protected UserStorage userStorage;

	protected Review addReviewBase() {
		Film film = Film.builder()
				.name("review-service-addReview-film-1")
				.description("review-service-addReview-film-1")
				.build();
		User user = User.builder()
				.login("review-service-addReview-user-1")
				.email("email@email.ru")
				.build();
		try {
			film = filmStorage.add(film);
			user = userStorage.add(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Review review = Review.builder()
				.filmId(film.getId())
				.userId(user.getId())
				.isPositive(true)
				.content("Test")
				.build();
		try {
			review = reviewService.addReview(review);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		return review;
	}

	@Test
	void addReview() {
		addReviewBase();
	}

	@Test
	void deleteReview() {
		Review review = addReviewBase();
		try {
			reviewService.deleteReview(review.getReviewId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	protected ReviewLike addReviewLikeBase(Review review, Boolean isLike) {
		User user = User.builder()
				.login("review-service-addReview-user-2")
				.email("email@email.ru")
				.build();
		try {
			user = userStorage.add(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		ReviewLike reviewLike = ReviewLike.builder()
				.reviewId(review.getReviewId())
				.userId(user.getId())
				.isLike(isLike)
				.build();
		try {
			reviewService.addLike(reviewLike);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		return reviewLike;
	}

	@Test
	void addReviewLike() {
		Review review = addReviewBase();
		Assertions.assertEquals(0, review.getUseful(), "Полезность должна быть равно 0");
		ReviewLike reviewLike = addReviewLikeBase(review, true);
		Assertions.assertEquals(1, review.getUseful(), "Полезность должна быть равно 1");
		ReviewLike reviewDislike = addReviewLikeBase(review, false);
		Assertions.assertEquals(0, review.getUseful(), "Полезность должна быть равно 0");
	}

	@Test
	void deleteReviewLike() {
		Review review = addReviewBase();
		Assertions.assertEquals(0, review.getUseful(), "Полезность должна быть равно 0");
		ReviewLike reviewLike = addReviewLikeBase(review, true);
		Assertions.assertEquals(1, review.getUseful(), "Полезность должна быть равно 1");
		try {
			reviewService.deleteLike(reviewLike);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Assertions.assertEquals(0, review.getUseful(), "Полезность должна быть равно 0");
	}
}
