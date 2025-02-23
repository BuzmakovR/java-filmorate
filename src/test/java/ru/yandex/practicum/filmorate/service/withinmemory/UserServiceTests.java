package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

	@Test
	void addUser() {
		User user1 = User.builder()
				.login("user-1")
				.build();

		User userFromService = null;
		try {
			user1 = userService.addUser(user1);
			userFromService = userService.getUser(user1.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Assertions.assertEquals(user1, userFromService, "Полученный пользователь не соответствует добавленному");
	}

	@Test
	void updateUser() {
		User user = User.builder()
				.login("user-1")
				.build();
		try {
			userService.addUser(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		User userUpdate = User.builder()
				.login("user-1-updated")
				.build();
		userUpdate.setId(user.getId());

		try {
			userService.updateUser(userUpdate);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		User userFromStorage = null;
		try {
			userFromStorage = userService.getUser(userUpdate.getId());
		} catch (Exception e) {
			Assertions.fail("Не удалось получить пользователя по ID");
		}
		Assertions.assertNotNull(userFromStorage, "Не удалось получить пользователя по ID");
		Assertions.assertEquals(userUpdate, userFromStorage, "Полученный пользователя не соответствует обновленному");

		final User user2Update = User.builder()
				.login("user-2-not-exists")
				.build();
		assertThrows(ValidationException.class, () -> {
			userService.updateUser(user2Update);
		}, "Не получено исключение валидации при обновлении без ID");

		user2Update.setId(999L);
		assertThrows(NotFoundException.class, () -> {
			userService.updateUser(user2Update);
		}, "Не получено исключение NotFoundException");
	}

	@Test
	void deleteUsers() {
		User user = User.builder()
				.login("user-1")
				.build();
		try {
			user = userService.addUser(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		userService.deleteUser(userId);

		assertThrows(NotFoundException.class, () -> {
			userService.getUser(userId);
		}, "Не удалось удалить пользователя");
	}
}
