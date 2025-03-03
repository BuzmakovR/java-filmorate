package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

public abstract class MpaRatingServiceTests {

	@Autowired
	protected MpaRatingService mpaRatingService;

	@Autowired
	protected MpaRatingStorage mpaRatingStorage;

	@BeforeEach
	protected void initStorage() {
	}

	@Test
	void getRatings() {
		try {
			mpaRatingService.getRatings();
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void getRating() {
		try {
			final Long ratingId = 1L;
			mpaRatingService.getRating(ratingId);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
