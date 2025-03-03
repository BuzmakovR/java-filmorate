package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

public abstract class FilmServiceTests {

	@Autowired
	protected FilmService filmService;

	@Autowired
	protected FilmStorage filmStorage;

	@Autowired
	protected UserStorage userStorage;

	@Autowired
	protected FilmLikeStorage filmLikeStorage;

	@Autowired
	protected GenreStorage genreStorage;

	@Autowired
	protected MpaRatingStorage mpaRatingStorage;

	@BeforeEach
	protected void initStorage() {
	}

	@Test
	void addLike() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.addLike(-1L, -1L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("film-service-addlike-user").email("email@email.ru").build();
		Film film = Film.builder().name("film").build();
		try {
			user = userStorage.add(user);
			film = filmService.addFilm(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		final Long filmId = film.getId();
		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.addLike(-1L, userId);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.addLike(filmId, -1L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		try {
			filmService.addLike(filmId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении лайка к фильму");
		}

		Film filmFromStorage = null;
		try {
			filmFromStorage = filmService.getFilm(filmId);
		} catch (Exception e) {
			Assertions.fail("Не удалось получить фильм по ID");
		}
		Assertions.assertNotNull(filmFromStorage, "Не удалось получить фильм по ID");
	}

	@Test
	void deleteLike() {
		User user = User.builder().login("film-service-deletelike-user").email("email@email.ru").build();
		Film film = Film.builder().name("film").build();
		try {
			user = userStorage.add(user);
			film = filmService.addFilm(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		final Long filmId = film.getId();

		try {
			filmService.addLike(filmId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении лайка к фильму");
		}
		Film filmFromStorage = null;
		try {
			filmFromStorage = filmService.getFilm(filmId);
		} catch (Exception e) {
			Assertions.fail("Не удалось получить фильм по ID");
		}
		Assertions.assertNotNull(filmFromStorage, "Не удалось получить фильм по ID");

		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.deleteLike(-1L, -1L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.deleteLike(filmId, -1L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.deleteLike(-1L, userId);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		try {
			filmService.deleteLike(filmId, userId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при удалении лайка к фильму");
		}
	}

	@Test
	void getPopularFilms() {
		filmService.getFilms()
				.forEach(film -> filmStorage.delete(film.getId()));

		User user1 = User.builder().login("film-service-getpopular-user-1").email("email@email.ru").build();
		User user2 = User.builder().login("film-service-getpopular-user-2").email("email@email.ru").build();
		Film film1 = Film.builder().name("film-1").build();
		Film film2 = Film.builder().name("film-2").build();
		Film film3 = Film.builder().name("film-3").build();

		try {
			user1 = userStorage.add(user1);
			user2 = userStorage.add(user2);
			film1 = filmService.addFilm(film1);
			film2 = filmService.addFilm(film2);
			film3 = filmService.addFilm(film3);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		/*
		 * 1 фильму - 0 лайков
		 * 2 фильму - 2 лайков
		 * 3 фильму - 1 лайков
		 *
		 * Результат популярности должен быть:
		 * 2 фильм
		 * 3 фильм
		 * 1 фильм
		 * */
		try {
			filmService.addLike(film2.getId(), user1.getId());
			filmService.addLike(film2.getId(), user2.getId());
			filmService.addLike(film3.getId(), user2.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		List<Film> popularFilms = filmService.getPopularFilms(3).stream().toList();
		Assertions.assertEquals(3, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film2, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film3, popularFilms.get(1), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film1, popularFilms.get(2), "Последовательность популярности фильмов некорректная");
	}
}
