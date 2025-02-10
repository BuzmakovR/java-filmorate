package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

	private final Map<Long, User> users = new HashMap<>();

	@Override
	public Collection<User> getAll() {
		return List.copyOf(users.values());
	}

	@Override
	public User get(Long id) {
		if (!users.containsKey(id)) {
			throw new NotFoundException("Пользователь с id = " + id + " не найден");
		}
		return users.get(id);
	}

	@Override
	public User add(User user) {
		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}
		user.validate();
		user.setId(getNextId());
		users.put(user.getId(), user);

		return user;
	}

	@Override
	public User update(User newUser) {
		if (newUser.getId() == null) {
			throw new ValidationException("Id пользователя должен быть указан");
		}

		User currentUser = get(newUser.getId());
		if (newUser.getName() == null || newUser.getName().isBlank()) {
			newUser.setName(newUser.getLogin());
		}
		for (Long userId : currentUser.getFriendIds()) {
			newUser.addFriend(userId);
		}

		users.put(newUser.getId(), newUser);
		return newUser;
	}

	@Override
	public User delete(Long id) {
		Optional<User> optionalUser = Optional.ofNullable(users.remove(id));
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			for (Long friendId : optionalUser.get().getFriendIds()) {
				Optional<User> optionalFriend = Optional.ofNullable(users.get(friendId));
				optionalFriend.ifPresent(friend -> {
					friend.deleteFriend(user.getId());
					users.put(friend.getId(), friend);
				});
			}
			return user;
		}
		return null;
	}

	private long getNextId() {
		long currentMaxId = users.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}
