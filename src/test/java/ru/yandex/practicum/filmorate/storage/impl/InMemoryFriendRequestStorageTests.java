package ru.yandex.practicum.filmorate.storage.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryFriendRequestStorage;

import java.util.Collection;
import java.util.List;

public class InMemoryFriendRequestStorageTests {

	private InMemoryFriendRequestStorage friendStorage;

	@BeforeEach
	void initStorage() {
		try {
			friendStorage = new InMemoryFriendRequestStorage();
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void addFriend() {
		Long userId = 1L;
		Long friendId2 = 2L;
		Long friendId3 = 3L;
		try {
			friendStorage.addUserFriend(userId, friendId2);
			friendStorage.addUserFriend(userId, friendId3);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Collection<Long> friendIds = friendStorage.getUserFriendIds(userId);
		Assertions.assertNotNull(friendIds, "Получен некорректный список IDs друзей равный null");
		Assertions.assertEquals(List.of(friendId2, friendId3), friendIds.stream().toList(), "Получен некорректный список IDs друзей");
	}

	@Test
	void deleteFriend() {
		Long userId = 1L;
		Long friendId2 = 2L;
		Long friendId3 = 3L;
		try {
			friendStorage.addUserFriend(userId, friendId2);
			friendStorage.addUserFriend(userId, friendId3);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Collection<Long> friendIds = friendStorage.getUserFriendIds(userId);
		Assertions.assertNotNull(friendIds, "Получен некорректный список IDs друзей равный null");
		Assertions.assertEquals(2, friendIds.size(), "Получен некорректный список IDs друзей");

		friendStorage.deleteUserFriend(userId, friendId2);
		friendIds = friendStorage.getUserFriendIds(userId);
		Assertions.assertNotNull(friendIds, "Получен некорректный список IDs друзей равный null");
		Assertions.assertEquals(1, friendIds.size(), "Получен некорректный список IDs друзей");

		friendStorage.deleteUserFriend(userId, friendId2);
		friendIds = friendStorage.getUserFriendIds(userId);
		Assertions.assertNotNull(friendIds, "Получен некорректный список IDs друзей равный null");
		Assertions.assertEquals(0, friendIds.size(), "Получен некорректный список IDs друзей");
	}

	@Test
	void getCommonFriendIds() {
		Long userId1 = 1L;
		Long userId2 = 2L;
		Long commonIds3 = 3L;
		try {
			friendStorage.addUserFriend(userId1, commonIds3);
			friendStorage.addUserFriend(userId2, commonIds3);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Collection<Long> commonIds = friendStorage.getCommonFriendIds(userId1, userId2);
		Assertions.assertNotNull(commonIds, "Получен некорректный список IDs общих друзей равный null");
		Assertions.assertEquals(1, commonIds.size(), "Получен некорректный список IDs общих друзей");
		Assertions.assertEquals(List.of(commonIds3), commonIds, "Получен некорректный список IDs общих друзей");
	}
}
