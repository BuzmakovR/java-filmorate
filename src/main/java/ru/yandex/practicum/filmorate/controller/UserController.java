package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	private final HashMap<Long, User> users = new HashMap<>();

	@GetMapping
	public Collection<User> findAll() {
		log.info("Запрос на получение пользователей");
		log.debug("Список пользователей: {}", users);
		return users.values();
	}

	@PostMapping
	public User create(@Valid @RequestBody User user) {
		log.info("Запрос на создание пользователя");
		log.debug(user.toString());

		if (user.getName() == null || user.getName().isBlank()) {
			log.info("При создании имя не указано. В качестве значения указывается логин");
			user.setName(user.getLogin());
		}
		user.validate();

		user.setId(getNextId());
		users.put(user.getId(), user);

		log.info("Пользователь создан");
		log.debug(user.toString());

		return user;
	}

	@PutMapping
	public User update(@RequestBody User newUser) {
		log.info("Запрос на обновление пользователя");

		if (newUser.getId() == null) {
			log.error("При обновлении пользователя Id не указан");
			throw new ValidationException("Id пользователя должен быть указан");
		}
		if (users.containsKey(newUser.getId())) {
			if (newUser.getName() == null || newUser.getName().isBlank()) {
				log.info("При обновлении имя не указано. В качестве значения указывается логин");
				newUser.setName(newUser.getLogin());
			}
			users.put(newUser.getId(), newUser);

			log.info("Пользователь обновлен");
			log.debug(newUser.toString());

			return newUser;
		}
		throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
	}

	private long getNextId() {
		long currentMaxId = users.values().stream().mapToLong(User::getId).max().orElse(0);
		return ++currentMaxId;
	}

}
