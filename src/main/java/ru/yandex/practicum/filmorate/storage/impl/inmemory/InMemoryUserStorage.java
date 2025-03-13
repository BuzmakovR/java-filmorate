package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component("inMemoryUserStorage")
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
		user.setId(getNextId());
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(User newUser) {
		if (newUser.getId() == null) {
			throw new ValidationException("Id пользователя должен быть указан");
		}
		if (!users.containsKey(newUser.getId())) {
			throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
		}
		users.put(newUser.getId(), newUser);
		return newUser;
	}

	@Override
	public User delete(Long id) {
		Optional<User> optionalUser = Optional.ofNullable(users.remove(id));
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("Пользователь с id = " + id + " не найден");
		}
		return optionalUser.get();
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
