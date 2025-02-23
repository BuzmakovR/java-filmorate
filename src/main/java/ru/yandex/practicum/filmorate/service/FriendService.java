package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendRequestStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Service
public class FriendService {

	@Qualifier("friendRequestDbStorage")
	private final FriendRequestStorage friendRequestStorage;

	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	public FriendService(@Qualifier("friendRequestDbStorage") FriendRequestStorage friendRequestStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
		this.friendRequestStorage = friendRequestStorage;
		this.userStorage = userStorage;
	}

	public Collection<User> getFriends(Long userId) {
		userStorage.get(userId);
		return friendRequestStorage.getUserFriendIds(userId)
				.stream()
				.map(userStorage::get)
				.toList();
	}

	public void addFriend(Long userId, Long friendId) {
		userStorage.get(userId);
		userStorage.get(friendId);

		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		friendRequestStorage.addUserFriend(userId, friendId);
	}

	public void deleteFriend(Long userId, Long friendId) {
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		userStorage.get(userId);
		userStorage.get(friendId);
		friendRequestStorage.deleteUserFriend(userId, friendId);
	}

	public Collection<User> getCommonFriends(Long userId1, Long userId2) {
		if (Objects.equals(userId1, userId2)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		userStorage.get(userId1);
		userStorage.get(userId2);
		return friendRequestStorage.getCommonFriendIds(userId1, userId2)
				.stream()
				.map(userStorage::get)
				.toList();
	}
}
