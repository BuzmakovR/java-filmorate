package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

	@Autowired
	private final UserStorage userStorage;

	public Collection<User> getUsers() {
		return userStorage.getAll();
	}

	public User getUser(final Long userId) {
		return userStorage.get(userId);
	}

	public User addUser(User user) {
		return userStorage.add(user);
	}

	public User updateUser(User newUser) {
		return userStorage.update(newUser);
	}

	public User deleteUser(final Long userId) {
		return userStorage.delete(userId);
	}

	public Collection<User> getFriends(Long userId) {
		User user = userStorage.get(userId);
		return user.getFriendIds()
				.stream()
				.map(userStorage::get)
				.toList();
	}

	public void addFriend(Long userId, Long friendId) {
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		User user = userStorage.get(userId);
		User friend = userStorage.get(friendId);
		user.addFriend(friend.getId());
		friend.addFriend(user.getId());
	}

	public void deleteFriend(Long userId, Long friendId) {
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		User user = userStorage.get(userId);
		User friend = userStorage.get(friendId);
		user.deleteFriend(friend.getId());
		friend.deleteFriend(user.getId());
	}

	public Collection<User> getCommonFriends(Long userId1, Long userId2) {
		if (Objects.equals(userId1, userId2)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		Set<Long> friendUser1 = userStorage.get(userId1).getFriendIds();
		Set<Long> friendUser2 = userStorage.get(userId2).getFriendIds();
		return friendUser1
				.stream()
				.filter(friendUser2::contains)
				.map(userStorage::get)
				.toList();

	}
}
