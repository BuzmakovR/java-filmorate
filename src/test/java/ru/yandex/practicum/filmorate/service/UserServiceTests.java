package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.util.List;

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
	void addFried() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.addFriend(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("user").build();
		User friend = User.builder().login("friend").build();
		try {
			user = userService.getUserStorage().add(user);
			friend = userService.getUserStorage().add(friend);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		final Long friendId = friend.getId();

		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.addFriend(userId, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.addFriend(999L, friendId);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(ValidationException.class, () -> {
			userService.addFriend(userId, userId);
		}, "Не получено исключение валидации переданных равных ID");
		try {
			userService.addFriend(userId, friendId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении в друзья");
		}
		User userFromStorage = null;
		User friendsFromStorage = null;
		try {
			userFromStorage = userService.getUserStorage().get(userId);
			friendsFromStorage = userService.getUserStorage().get(friendId);
		} catch (Exception e) {
			Assertions.fail("Не удалось получить пользователя по ID");
		}

		Assertions.assertNotNull(userFromStorage, "Не удалось получить пользователя по ID");
		Assertions.assertNotNull(userFromStorage.getFriendIds(), "Список друзей пользователя равен null");
		Assertions.assertFalse(userFromStorage.getFriendIds().isEmpty(), "Список друзей пользователя пустой");

		Assertions.assertNotNull(friendsFromStorage, "Не удалось получить пользователя по ID");
		Assertions.assertNotNull(friendsFromStorage.getFriendIds(), "Список друзей пользователя равен null");
		Assertions.assertFalse(friendsFromStorage.getFriendIds().isEmpty(), "Список друзей пользователя пустой");
	}

	@Test
	void deleteFried() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.deleteFriend(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("user").build();
		User friend = User.builder().login("friend").build();
		try {
			user = userService.getUserStorage().add(user);
			friend = userService.getUserStorage().add(friend);
			userService.addFriend(user.getId(), friend.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		final Long friendId = friend.getId();

		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.deleteFriend(userId, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.deleteFriend(999L, friendId);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(ValidationException.class, () -> {
			userService.deleteFriend(userId, userId);
		}, "Не получено исключение валидации переданных равных ID");
		try {
			userService.deleteFriend(userId, friendId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при удаление из друзей");
		}
		User userFromStorage = null;
		User friendsFromStorage = null;
		try {
			userFromStorage = userService.getUserStorage().get(userId);
			friendsFromStorage = userService.getUserStorage().get(friendId);
		} catch (Exception e) {
			Assertions.fail("Не удалось получить пользователя по ID");
		}
		Assertions.assertNotNull(userFromStorage, "Не удалось получить пользователя по ID");
		Assertions.assertNotNull(userFromStorage.getFriendIds(), "Список друзей пользователя равен null");
		Assertions.assertTrue(userFromStorage.getFriendIds().isEmpty(), "Список друзей пользователя не пустой");

		Assertions.assertNotNull(friendsFromStorage, "Не удалось получить пользователя по ID");
		Assertions.assertNotNull(friendsFromStorage.getFriendIds(), "Список друзей пользователя равен null");
		Assertions.assertTrue(friendsFromStorage.getFriendIds().isEmpty(), "Список друзей пользователя не пустой");
	}

	@Test
	void getCommonFriends() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.getCommonFriends(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user1 = User.builder().login("user1").build();
		User user2 = User.builder().login("user2").build();
		User commonFriend = User.builder().login("commonFriend").build();
		try {
			user1 = userService.getUserStorage().add(user1);
			user2 = userService.getUserStorage().add(user2);
			commonFriend = userService.getUserStorage().add(commonFriend);
			userService.addFriend(user1.getId(), commonFriend.getId());
			userService.addFriend(user2.getId(), commonFriend.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long user1Id = user1.getId();
		final Long user2Id = user2.getId();

		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.getCommonFriends(user1Id, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.getCommonFriends(999L, user2Id);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(ValidationException.class, () -> {
			userService.getCommonFriends(user1Id, user1Id);
		}, "Не получено исключение валидации переданных равных ID");

		List<User> commonFriends = List.of();
		try {
			commonFriends = userService.getCommonFriends(user1Id, user2Id).stream().toList();
		} catch (Exception e) {
			Assertions.fail("Получено исключение при получении общих друзей");
		}
		Assertions.assertEquals(1, commonFriends.size(), "Не удалось получить общего друга");
		Assertions.assertEquals(commonFriend, commonFriends.getFirst(), "Значение полученное в качестве общего друга неверное");
	}
}
