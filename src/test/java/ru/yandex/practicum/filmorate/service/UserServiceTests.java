package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendRequestStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class UserServiceTests {

	@Autowired
	protected UserService userService;

	@Autowired
	protected UserStorage userStorage;

	@Autowired
	protected FriendRequestStorage friendRequestStorage;

	@BeforeEach
	protected void initStorage() {
	}

	@Test
	void addUser() {
		User user1 = User.builder()
				.login("user-service-add-user-1")
				.email("email@email.ru")
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
				.login("user-service-update-user-1")
				.email("email@email.ru")
				.build();
		try {
			userService.addUser(user);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		User userUpdate = User.builder()
				.login("user-service-update-user-1-updated")
				.email("email@email.ru")
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
				.login("user-service-update-user-2-not-exists")
				.email("email@email.ru")
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
				.login("user-service-delete-user-1")
				.email("email@email.ru")
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

	@Test
	void addFriend() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.addFriend(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("friend-service-add-user").email("email@email.ru").build();
		User friend = User.builder().login("friend-service-add-friend").email("email@email.ru").build();
		try {
			user = userStorage.add(user);
			friend = userStorage.add(friend);
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

		Collection<User> friends = userService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");

		friends = userService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");

		try {
			userService.addFriend(friendId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении в друзья");
		}
		friends = userService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");

		friends = userService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");
	}

	@Test
	void deleteFriend() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.deleteFriend(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("friend-service-delete-user").email("email@email.ru").build();
		User friend = User.builder().login("friend-service-delete-friend").email("email@email.ru").build();
		try {
			user = userStorage.add(user);
			friend = userStorage.add(friend);
			userService.addFriend(user.getId(), friend.getId());
			userService.addFriend(friend.getId(), user.getId());
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

		Collection<User> friends = userService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");

		friends = userService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");

		try {
			userService.deleteFriend(friendId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при удаление из друзей");
		}
		friends = userService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");

		friends = userService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");
	}

	@Test
	void getCommonFriends() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.getCommonFriends(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user1 = User.builder().login("friend-service-getcommon-user1").email("email@email.ru").build();
		User user2 = User.builder().login("friend-service-getcommon-user2").email("email@email.ru").build();
		User commonFriend = User.builder().login("friend-service-getcommon-commonFriend").email("email@email.ru").build();
		try {
			user1 = userStorage.add(user1);
			user2 = userStorage.add(user2);
			commonFriend = userStorage.add(commonFriend);
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
