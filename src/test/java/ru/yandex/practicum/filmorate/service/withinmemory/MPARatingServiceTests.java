package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.MPARatingService;
import ru.yandex.practicum.filmorate.storage.MPARatingStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryMPARatingStorage;

public class MPARatingServiceTests {

	MPARatingService mpaRatingService;

	@BeforeEach
	void initStorage() {
		try {
			MPARatingStorage mpaRatingStorage = new InMemoryMPARatingStorage();
			mpaRatingService = new MPARatingService(mpaRatingStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
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
