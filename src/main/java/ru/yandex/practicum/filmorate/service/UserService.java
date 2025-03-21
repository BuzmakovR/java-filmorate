package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendRequestStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	@Qualifier("friendRequestDbStorage")
	private final FriendRequestStorage friendRequestStorage;

	@Qualifier("feedDbStorage")
	private final FeedStorage feedStorage;

	@Qualifier("filmLikeDbStorage")
	private final FilmLikeStorage filmLikeStorage;

	@Qualifier("filmDbStorage")
	private final FilmStorage filmStorage;

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
		feedStorage.addEvent(userId, friendId, EventOperation.ADD, EventType.FRIEND);
	}

	public void deleteFriend(Long userId, Long friendId) {
		if (Objects.equals(userId, friendId)) {
			throw new ValidationException("Идентификаторы пользователей не могут быть равны");
		}
		userStorage.get(userId);
		userStorage.get(friendId);
		friendRequestStorage.deleteUserFriend(userId, friendId);
		feedStorage.addEvent(userId, friendId, EventOperation.REMOVE, EventType.FRIEND);
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

	public Set<Film> getRecommendationFilmsByUserId(long userId) {
		log.info("Запрос рекомендаций для пользователя с id: {}", userId);
		userStorage.get(userId);

		log.info("Запрос пользователей с одинаковыми лайками для фильмов");
		Set<Long> usersWithSameLikes = filmLikeStorage.getUsersWithSameLikes(userId);
		Set<Film> recommendedFilms = Set.copyOf(filmStorage.getRecommendationFilmsByUserId(userId, usersWithSameLikes));

		log.info("Рекомендуемые фильмы: {}", recommendedFilms);
		return recommendedFilms;
	}

	public Collection<Feed> getFeed(Long userId) {
		log.info("Запрос ленты событий для пользователя с id: {}", userId);
		userStorage.get(userId);
		return feedStorage.getFeed(userId);
	}
}
