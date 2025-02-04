package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {

	@Autowired
	private final FilmStorage filmStorage;

	@Autowired
	private final UserStorage userStorage;

	public void addLike(Long filmId, Long userId) {
		Optional<Film> optionalFilm = filmStorage.get(filmId);
		if (optionalFilm.isEmpty()) {
			throw new NotFoundException("Фильм с id = " + filmId + " не найден");
		}
		Optional<User> optionalUser = userStorage.get(userId);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("Пользователь с id = " + userId + " не найден");
		}
		optionalFilm.get().getUserLikes().add(optionalUser.get().getId());
	}

	public void deleteLike(Long filmId, Long userId) {
		Optional<Film> optionalFilm = filmStorage.get(filmId);
		if (optionalFilm.isEmpty()) {
			throw new NotFoundException("Фильм с id = " + filmId + " не найден");
		}
		Optional<User> optionalUser = userStorage.get(userId);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("Пользователь с id = " + userId + " не найден");
		}
		optionalFilm.get().getUserLikes().remove(optionalUser.get().getId());
	}

	public Collection<Film> getPopularFilms(Integer count) {
		if (count < 1) {
			throw new ValidationException("Значение переданного параметра количество записей должен быть больше 0");
		}
		return filmStorage.getAll()
				.stream()
				.sorted(Collections.reverseOrder(Comparator.comparing(film -> film.getUserLikes().size())))
				.limit(count)
				.toList();
	}
}
