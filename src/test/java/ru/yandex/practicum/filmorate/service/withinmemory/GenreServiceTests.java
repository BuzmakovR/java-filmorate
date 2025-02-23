package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryGenreStorage;

public class GenreServiceTests {

	GenreService genreService;

	@BeforeEach
	void initStorage() {
		try {
			GenreStorage genreStorage = new InMemoryGenreStorage();
			genreService = new GenreService(genreStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void getGenres() {
		try {
			genreService.getGenres();
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void getGenre() {
		try {
			final Long genreId = 1L;
			genreService.getGenre(genreId);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
