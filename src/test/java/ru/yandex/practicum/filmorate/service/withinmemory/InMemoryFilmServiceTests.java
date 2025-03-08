package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.*;

public class InMemoryFilmServiceTests extends FilmServiceTests {

	@BeforeEach
	@Override
	protected void initStorage() {
		try {
			userStorage = new InMemoryUserStorage();
			filmLikeStorage = new InMemoryFilmLikeStorage();
			filmStorage = new InMemoryFilmStorage((InMemoryFilmLikeStorage) filmLikeStorage);
			genreStorage = new InMemoryGenreStorage();
			mpaRatingStorage = new InMemoryMpaRatingStorage();
			filmService = new FilmService(filmStorage,
					filmLikeStorage,
					userStorage,
					genreStorage,
					mpaRatingStorage,
					directorStorage
			);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
