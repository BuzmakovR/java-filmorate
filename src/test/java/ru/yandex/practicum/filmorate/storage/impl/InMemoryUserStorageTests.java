package ru.yandex.practicum.filmorate.storage.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryUserStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryUserStorageTests {

	private InMemoryUserStorage userStorage;

	@BeforeEach
	void initStorage() {
		try {
			userStorage = new InMemoryUserStorage();
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void addUser() {
		User user1 = User.builder()
				.login("user-1")
				.build();

		try {
			userStorage.add(user1);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void getUsers() {
		User user1 = User.builder()
				.login("user-1")
				.build();
		User user2 = User.builder()
				.login("user-2")
				.build();
		try {
			userStorage.add(user1);
			userStorage.add(user2);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		List<User> users = userStorage.getAll().stream().toList();
		Assertions.assertEquals(2, users.size(), "Кол-во фильмов не соответствует добавленному кол-ву");
		Assertions.assertEquals(user1, users.getFirst(), "Добавленный 1 пользователя не соответствует полученному");
		Assertions.assertEquals(user2, users.get(1), "Добавленный 2 пользователя не соответствует полученному");

		User userFromStorage2 = null;
		try {
			userFromStorage2 = userStorage.get(user2.getId());
		} catch (Exception e) {
			Assertions.fail("Не удалось получить пользователя по ID");
		}
		Assertions.assertNotNull(userFromStorage2, "Не удалось получить пользователя по ID");
		Assertions.assertEquals(user2, userFromStorage2, "Добавленный 2 пользователя не соответствует полученному");
	}

	@Test
	void updateUsers() {
		User user = User.builder()
				.login("user-1")
				.build();
		try {
			userStorage.add(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		User userUpdate = User.builder()
				.login("user-1-updated")
				.build();
		userUpdate.setId(user.getId());

		try {
			userStorage.update(userUpdate);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		User userFromStorage = null;
		try {
			userFromStorage = userStorage.get(userUpdate.getId());
		} catch (Exception e) {
			Assertions.fail("Не удалось получить пользователя по ID");
		}
		Assertions.assertNotNull(userFromStorage, "Не удалось получить пользователя по ID");
		Assertions.assertEquals(userUpdate, userFromStorage, "Полученный пользователя не соответствует обновленному");

		final User user2Update = User.builder()
				.login("user-2-not-exists")
				.build();
		assertThrows(ValidationException.class, () -> {
			userStorage.update(user2Update);
		}, "Не получено исключение валидации при обновлении без ID");

		user2Update.setId(999L);
		assertThrows(NotFoundException.class, () -> {
			userStorage.update(user2Update);
		}, "Не получено исключение NotFoundException");
	}

	@Test
	void deleteUsers() {
		User user = User.builder()
				.login("user-1")
				.build();
		try {
			user = userStorage.add(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		userStorage.delete(userId);

		assertThrows(NotFoundException.class, () -> {
			userStorage.get(userId);
		}, "Не удалось удалить пользователя");
	}
}
