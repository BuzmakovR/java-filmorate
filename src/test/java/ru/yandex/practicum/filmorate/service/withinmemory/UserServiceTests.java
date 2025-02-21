package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryUserStorage;

public class UserServiceTests {

	private UserService userService;

	@BeforeEach
	void initStorage() {
		try {
			UserStorage userStorage = new InMemoryUserStorage();
			userService = new UserService(userStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}


}
