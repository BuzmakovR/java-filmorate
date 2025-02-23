package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {

	@Autowired
	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	@Autowired
	public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
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
		return userStorage.delete(userId);
	}

}
