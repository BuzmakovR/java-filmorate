package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

	@Autowired
	private final UserStorage userStorage;

	public Collection<User> getFriends(Long userId) {
		User user = getUser(userId);
		return user.getFriendIds()
				.stream()
				.map(this::getUser)
				.toList();
	}

	public void addFriend(Long userId, Long friendId) {
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		User user = getUser(userId);
		User friend = getUser(friendId);
		user.getFriendIds().add(friend.getId());
		friend.getFriendIds().add(user.getId());
	}

	public void deleteFriend(Long userId, Long friendId) {
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		User user = getUser(userId);
		User friend = getUser(friendId);
		user.getFriendIds().remove(friend.getId());
		friend.getFriendIds().remove(user.getId());
	}

	public Collection<User> getCommonFriends(Long userId1, Long userId2) {
		if (Objects.equals(userId1, userId2)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		Set<Long> friendUser1 = getUser(userId1).getFriendIds();
		Set<Long> friendUser2 = getUser(userId2).getFriendIds();
		return friendUser1
				.stream()
				.filter(friendUser2::contains)
				.map(userStorage::get)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();

	}

	private User getUser(Long userId) {
		Optional<User> optionalUser = userStorage.get(userId);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("Пользователь с id = " + userId + " не найден");
		}
		return optionalUser.get();
	}
}
