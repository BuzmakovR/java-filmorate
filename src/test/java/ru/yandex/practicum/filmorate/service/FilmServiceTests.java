package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.util.List;

public class FilmServiceTests {

	private FilmService filmService;

	@BeforeEach
	void initStorage() {
		try {
			FilmStorage filmStorage = new InMemoryFilmStorage();
			UserStorage userStorage = new InMemoryUserStorage();
			filmService = new FilmService(filmStorage, userStorage);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void addLike() {
		Assertions.assertThrows(NotFoundException.class, () -> {
			filmService.addLike(-1L, -1L);
		}, "Не получено исключение NotFoundException при передаче несуществующих ID");

		User user = User.builder().login("user").build();
		Film film = Film.builder().name("film").build();
		try {
			user = filmService.getUserStorage().add(user);
			film = filmService.getFilmStorage().add(film);
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
			filmFromStorage = filmService.getFilmStorage().get(filmId);
		} catch (Exception e) {
			Assertions.fail("Не удалось получить фильм по ID");
		}
		Assertions.assertNotNull(filmFromStorage, "Не удалось получить фильм по ID");
		Assertions.assertFalse(filmFromStorage.getUserLikes().isEmpty(), "Список лайков фильма пустой");
	}

	@Test
	void deleteLike() {
		User user = User.builder().login("user").build();
		Film film = Film.builder().name("film").build();
		try {
			user = filmService.getUserStorage().add(user);
			film = filmService.getFilmStorage().add(film);
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
			filmFromStorage = filmService.getFilmStorage().get(filmId);
		} catch (Exception e) {
			Assertions.fail("Не удалось получить фильм по ID");
		}
		Assertions.assertNotNull(filmFromStorage, "Не удалось получить фильм по ID");
		Assertions.assertFalse(filmFromStorage.getUserLikes().isEmpty(), "Список лайков фильма пустой");

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
		Assertions.assertTrue(filmFromStorage.getUserLikes().isEmpty(), "После удаления список лайков фильма не пустой");
	}

	@Test
	void getPopularFilms() {
		User user1 = User.builder().login("user-1").build();
		User user2 = User.builder().login("user-2").build();
		Film film1 = Film.builder().name("film-1").build();
		Film film2 = Film.builder().name("film-2").build();
		Film film3 = Film.builder().name("film-3").build();

		try {
			user1 = filmService.getUserStorage().add(user1);
			user2 = filmService.getUserStorage().add(user2);
			film1 = filmService.getFilmStorage().add(film1);
			film2 = filmService.getFilmStorage().add(film2);
			film3 = filmService.getFilmStorage().add(film3);
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
