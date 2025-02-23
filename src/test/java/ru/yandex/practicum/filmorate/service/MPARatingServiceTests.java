package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.storage.MPARatingStorage;

public abstract class MPARatingServiceTests {

	@Autowired
	protected MPARatingService mpaRatingService;

	@Autowired
	protected MPARatingStorage mpaRatingStorage;

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
