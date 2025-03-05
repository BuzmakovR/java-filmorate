package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.*;

public class InMemoryUserServiceTests extends UserServiceTests {

	@BeforeEach
	@Override
	public void initStorage() {
		try {
			userStorage = new InMemoryUserStorage();
			friendRequestStorage = new InMemoryFriendRequestStorage();
			filmService = new FilmService(
					new InMemoryFilmStorage(new InMemoryFilmLikeStorage()),
					new InMemoryFilmLikeStorage(),
					userStorage,
					new InMemoryGenreStorage(),
					new InMemoryMpaRatingStorage()
			);
			userService = new UserService(userStorage, friendRequestStorage, filmService);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
