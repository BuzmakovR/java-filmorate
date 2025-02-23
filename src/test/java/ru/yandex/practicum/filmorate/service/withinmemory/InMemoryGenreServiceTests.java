package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.GenreServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryGenreStorage;

public class InMemoryGenreServiceTests extends GenreServiceTests {

	@BeforeEach
	@Override
	protected void initStorage() {
		try {
			genreStorage = new InMemoryGenreStorage();
			genreService = new GenreService(genreStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
