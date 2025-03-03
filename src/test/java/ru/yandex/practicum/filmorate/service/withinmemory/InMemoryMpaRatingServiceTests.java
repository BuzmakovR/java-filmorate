package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.service.MpaRatingServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryMpaRatingStorage;

public class InMemoryMpaRatingServiceTests extends MpaRatingServiceTests {

	@BeforeEach
	protected void initStorage() {
		try {
			mpaRatingStorage = new InMemoryMpaRatingStorage();
			mpaRatingService = new MpaRatingService(mpaRatingStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
