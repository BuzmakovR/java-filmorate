package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ReviewServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.*;

public class InMemoryReviewServiceTests extends ReviewServiceTests {

	@BeforeEach
	protected void initStorage() {
		try {
			reviewStorage = new InMemoryReviewStorage();
			filmLikeStorage = new InMemoryFilmLikeStorage();
			filmStorage = new InMemoryFilmStorage((InMemoryFilmLikeStorage) filmLikeStorage);
			userStorage = new InMemoryUserStorage();
			feedStorage = new InMemoryFeedStorage();
			reviewService = new ReviewService(reviewStorage, filmStorage, userStorage, feedStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
