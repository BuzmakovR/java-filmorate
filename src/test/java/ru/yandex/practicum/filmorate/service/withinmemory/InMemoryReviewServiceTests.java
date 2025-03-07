package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ReviewServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryFilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryReviewStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryUserStorage;

public class InMemoryReviewServiceTests extends ReviewServiceTests {

	@BeforeEach
	protected void initStorage() {
		try {
			reviewStorage = new InMemoryReviewStorage();
			filmLikeStorage = new InMemoryFilmLikeStorage();
			filmStorage = new InMemoryFilmStorage((InMemoryFilmLikeStorage) filmLikeStorage);
			userStorage = new InMemoryUserStorage();
			reviewService = new ReviewService(reviewStorage, filmStorage, userStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
