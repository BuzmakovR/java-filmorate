package ru.yandex.practicum.filmorate.service.withinmemory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.FriendServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryFriendRequestStorage;
import ru.yandex.practicum.filmorate.storage.impl.inmemory.InMemoryUserStorage;

public class InMemoryFriendServiceTests extends FriendServiceTests {

	@BeforeEach
	@Override
	public void initStorage() {
		try {
			userStorage = new InMemoryUserStorage();
			friendRequestStorage = new InMemoryFriendRequestStorage();
			friendService = new FriendService(friendRequestStorage, userStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
}
