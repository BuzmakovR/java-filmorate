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

public abstract class FriendServiceTests {

	@Autowired
	protected FriendService friendService;

	@Autowired
	protected UserStorage userStorage;

	@Autowired
	protected FriendRequestStorage friendRequestStorage;

	@BeforeEach
	protected void initStorage() {
	}

	@Test
	void addFriend() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.addFriend(999L, 1000L);
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
			friendService.addFriend(userId, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.addFriend(999L, friendId);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(ValidationException.class, () -> {
			friendService.addFriend(userId, userId);
		}, "Не получено исключение валидации переданных равных ID");
		try {
			friendService.addFriend(userId, friendId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении в друзья");
		}

		Collection<User> friends = friendService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");

		friends = friendService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");

		try {
			friendService.addFriend(friendId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении в друзья");
		}
		friends = friendService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");

		friends = friendService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");
	}

	@Test
	void deleteFriend() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.deleteFriend(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("friend-service-delete-user").email("email@email.ru").build();
		User friend = User.builder().login("friend-service-delete-friend").email("email@email.ru").build();
		try {
			user = userStorage.add(user);
			friend = userStorage.add(friend);
			friendService.addFriend(user.getId(), friend.getId());
			friendService.addFriend(friend.getId(), user.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		final Long friendId = friend.getId();

		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.deleteFriend(userId, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.deleteFriend(999L, friendId);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(ValidationException.class, () -> {
			friendService.deleteFriend(userId, userId);
		}, "Не получено исключение валидации переданных равных ID");
		try {
			friendService.deleteFriend(userId, friendId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при удаление из друзей");
		}

		Collection<User> friends = friendService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");

		friends = friendService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertFalse(friends.isEmpty(), "Список друзей пользователя пустой");

		try {
			friendService.deleteFriend(friendId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при удаление из друзей");
		}
		friends = friendService.getFriends(userId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");

		friends = friendService.getFriends(friendId);
		Assertions.assertNotNull(friends, "Список друзей пользователя равен null");
		Assertions.assertTrue(friends.isEmpty(), "Список друзей пользователя не пустой");
	}

	@Test
	void getCommonFriends() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.getCommonFriends(999L, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user1 = User.builder().login("friend-service-getcommon-user1").email("email@email.ru").build();
		User user2 = User.builder().login("friend-service-getcommon-user2").email("email@email.ru").build();
		User commonFriend = User.builder().login("friend-service-getcommon-commonFriend").email("email@email.ru").build();
		try {
			user1 = userStorage.add(user1);
			user2 = userStorage.add(user2);
			commonFriend = userStorage.add(commonFriend);
			friendService.addFriend(user1.getId(), commonFriend.getId());
			friendService.addFriend(user2.getId(), commonFriend.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long user1Id = user1.getId();
		final Long user2Id = user2.getId();

		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.getCommonFriends(user1Id, 1000L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(NotFoundException.class, () -> {
			friendService.getCommonFriends(999L, user2Id);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");
		Assertions.assertThrows(ValidationException.class, () -> {
			friendService.getCommonFriends(user1Id, user1Id);
		}, "Не получено исключение валидации переданных равных ID");

		List<User> commonFriends = List.of();
		try {
			commonFriends = friendService.getCommonFriends(user1Id, user2Id).stream().toList();
		} catch (Exception e) {
			Assertions.fail("Получено исключение при получении общих друзей");
		}
		Assertions.assertEquals(1, commonFriends.size(), "Не удалось получить общего друга");
		Assertions.assertEquals(commonFriend, commonFriends.getFirst(), "Значение полученное в качестве общего друга неверное");
	}
}
