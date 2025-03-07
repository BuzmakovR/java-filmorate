package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

	@Autowired
	private final ReviewService reviewService;

	//region REVIEW
	@GetMapping("/{id}")
	public Review get(@PathVariable("id") long id) {
		log.info("Запрос на получение отзыва с ID: {}", id);

		Review review = reviewService.getReview(id);

		log.debug("Полученный отзыв: {}", review);

		return review;
	}

	@GetMapping()
	public Collection<Review> friends(@RequestParam(name = "filmId") long filmId,
									  @RequestParam(name = "count", defaultValue = "10") int count) {
		return reviewService.getReviews(filmId, count);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Review create(@Valid @RequestBody Review review) {
		log.info("Запрос на создание отзыва");
		log.debug(review.toString());

		Review createdReview = reviewService.addReview(review);

		log.info("Отзыв создан c id {}", createdReview.getReviewId());
		log.debug(createdReview.toString());

		return review;
	}

	@PutMapping
	public Review update(@Valid @RequestBody Review newReview) {
		log.info("Запрос на обновление отзыва");
		log.debug(newReview.toString());

		Review updatedReview = reviewService.updateReview(newReview);

		log.info("Отзыв обновлен");
		log.debug(updatedReview.toString());

		return updatedReview;
	}

	@DeleteMapping("/{id}")
	public Review delete(@PathVariable("id") long reviewId) {
		return reviewService.deleteReview(reviewId);
	}
	//endregion

	//region REVIEW LIKE
	@PutMapping("/{id}/like/{userId}")
	public void addLike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		log.info("Пользователь с id {} поставил лайк отзыву {}", userId, reviewId);
		reviewService.addLike(
				ReviewLike.builder()
						.reviewId(reviewId)
						.userId(userId)
						.isLike(true)
						.build()
		);
	}

	@PutMapping("/{id}/dislike/{userId}")
	public void addDislike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		log.info("Пользователь с id {} поставил дизлайк отзыву {}", userId, reviewId);
		reviewService.addLike(
				ReviewLike.builder()
						.reviewId(reviewId)
						.userId(userId)
						.isLike(false)
						.build()
		);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public void deleteLike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		reviewService.deleteLike(
				ReviewLike.builder()
						.reviewId(reviewId)
						.userId(userId)
						.isLike(true)
						.build()
		);
	}

	@DeleteMapping("/{id}/dislike/{userId}")
	public void deleteDislike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		reviewService.deleteLike(
				ReviewLike.builder()
						.reviewId(reviewId)
						.userId(userId)
						.isLike(false)
						.build()
		);
	}

	//endregion
}
