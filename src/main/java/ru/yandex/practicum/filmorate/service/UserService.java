package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendRequestStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Service
public class UserService {

	@Autowired
	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	@Qualifier("friendRequestDbStorage")
	private final FriendRequestStorage friendRequestStorage;

	@Autowired
	public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
					   @Qualifier("friendRequestDbStorage") FriendRequestStorage friendRequestStorage) {
		this.friendRequestStorage = friendRequestStorage;
		this.userStorage = userStorage;
	}

	public Collection<User> getUsers() {
		return userStorage.getAll();
	}

	public User getUser(final Long userId) {
		return userStorage.get(userId);
	}

	public User addUser(User user) {
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}
		user.validate();
		return userStorage.add(user);
	}

	public User updateUser(User newUser) {
		if (newUser.getId() == null) {
			throw new ValidationException("Id пользователя должен быть указан");
		}
		if (newUser.getName() == null || newUser.getName().isBlank()) {
			newUser.setName(newUser.getLogin());
		}
		newUser.validate();
		return userStorage.update(newUser);
	}

	public User deleteUser(final Long userId) {
		User user = userStorage.delete(userId);
		friendRequestStorage.deleteAllFriendsRequestForUser(user.getId());
		return user;
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
