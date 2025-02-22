package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class FilmService {

	@Autowired
	private final FilmStorage filmStorage;

	@Autowired
	private final UserStorage userStorage;

	public Collection<Film> getFilms() {
		return filmStorage.getAll();
	}

	public Film getFilm(final Long filmId) {
		return filmStorage.get(filmId);
	}

	public Film addFilm(Film user) {
		return filmStorage.add(user);
	}

	public Film updateFilm(Film newFilm) {
		return filmStorage.update(newFilm);
	}

	public Film deleteFilm(final Long filmId) {
		return filmStorage.delete(filmId);
	}

	public void addLike(Long filmId, Long userId) {
		Film film = filmStorage.get(filmId);
		User user = userStorage.get(userId);
		film.addUserLike(user.getId());
	}

	public void deleteLike(Long filmId, Long userId) {
		Film film = filmStorage.get(filmId);
		User user = userStorage.get(userId);
		film.deleteUserLike(user.getId());
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
