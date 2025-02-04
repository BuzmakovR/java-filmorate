package ru.yandex.practicum.filmorate.storage.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

		Optional<User> optionalUser2 = userStorage.get(user2.getId());
		Assertions.assertTrue(optionalUser2.isPresent(), "Не удалось получить 2 пользователя по ID");
		Assertions.assertEquals(user2, optionalUser2.get(), "Добавленный 2 пользователя не соответствует полученному");
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

		User filmUpdate = User.builder()
				.login("user-1-updated")
				.build();
		filmUpdate.setId(user.getId());

		try {
			userStorage.update(filmUpdate);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		Optional<User> userUpdatedOptional = userStorage.get(filmUpdate.getId());
		Assertions.assertTrue(userUpdatedOptional.isPresent(), "Не удалось получить пользователя по ID");
		Assertions.assertEquals(filmUpdate, userUpdatedOptional.get(), "Полученный пользователя не соответствует обновленному");

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
		userStorage.delete(user.getId());
		Optional<User> userOptional = userStorage.get(user.getId());
		assertTrue(userOptional.isEmpty(), "Не удалось удалить пользователя");
	}
}
