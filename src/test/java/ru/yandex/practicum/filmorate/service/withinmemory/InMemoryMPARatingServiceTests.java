package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.MPARatingService;
import ru.yandex.practicum.filmorate.service.MPARatingServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryMPARatingStorage;

public class InMemoryMPARatingServiceTests extends MPARatingServiceTests {

	@BeforeEach
	protected void initStorage() {
		try {
			mpaRatingStorage = new InMemoryMPARatingStorage();
			mpaRatingService = new MPARatingService(mpaRatingStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
