package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	@Autowired
	private final UserStorage userStorage;

	@Autowired
	private final UserService userService;

	//region USER
	@GetMapping
	public Collection<User> findAll() {
		log.info("Запрос на получение пользователей");

		Collection<User> users = userStorage.getAll();

		log.debug("Список пользователей: {}", users);

		return users;
	}

	@GetMapping("/{id}")
	public User get(@PathVariable("id") long id) {
		log.info("Запрос на получение пользователя с ID: {}", id);

		User user = userStorage.get(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
		log.debug("Полученный пользователь: {}", user);

		return user;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public User create(@Valid @RequestBody User user) {
		log.info("Запрос на создание пользователя");
		log.debug(user.toString());

		User createdUser = userStorage.add(user);

		log.info("Пользователь создан");
		log.debug(createdUser.toString());

		return createdUser;
	}

	@PutMapping
	public User update(@Valid @RequestBody User newUser) {
		log.info("Запрос на обновление пользователя");

		User updatedUser = userStorage.update(newUser);

		log.info("Пользователь обновлен");
		log.debug(newUser.toString());

		return updatedUser;
	}

	@DeleteMapping("/{id}")
	public User delete(@PathVariable("id") long userId) {
		return userStorage.delete(userId);
	}
	//endregion

	//region USER-FRIENDS
	@GetMapping("/{id}/friends")
	public Collection<User> friends(@PathVariable("id") long userId) {
		return userService.getFriends(userId);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public Collection<User> commonFriends(@PathVariable("id") long userId, @PathVariable("otherId") long otherId) {
		return userService.getCommonFriends(userId, otherId);
	}

	@PutMapping("/{id}/friends/{friendId}")
	public void addFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
		userService.addFriend(userId, friendId);
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public void deleteFriend(@PathVariable("id") long userId, @PathVariable("friendId") long friendId) {
		userService.deleteFriend(userId, friendId);
	}
	//endregion
}
