package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryUserStorage;

public class InMemoryUserServiceTests extends UserServiceTests {

	@BeforeEach
	@Override
	public void initStorage() {
		try {
			userStorage = new InMemoryUserStorage();
			userService = new UserService(userStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
