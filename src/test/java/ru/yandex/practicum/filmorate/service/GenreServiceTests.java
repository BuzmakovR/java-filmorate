package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

public abstract class GenreServiceTests {

	@Autowired
	protected GenreStorage genreStorage;

	@Autowired
	protected GenreService genreService;

	@BeforeEach
	protected void initStorage() {
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
