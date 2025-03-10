package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

	@Autowired
	protected FeedStorage feedStorage;

	@BeforeEach
	protected void initStorage() {
	}

	@Test
	void addFilms() {
		Film film = Film.builder()
				.name("film-service-add-film-1")
				.description("film-service-add-film-1")
				.build();

		Film filmFromStorage = null;
		try {
			film = filmService.addFilm(film);
			filmFromStorage = filmService.getFilm(film.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		Assertions.assertEquals(film, filmFromStorage, "Полученный фильм не соответствует добавленному");
	}

	@Test
	void updateFilm() {
		Film film = Film.builder()
				.name("film-service-update-film-1")
				.description("film-service-update-film-1")
				.build();
		try {
			filmService.addFilm(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		Film filmUpdate = Film.builder()
				.name("film-service-update-film-1-updated")
				.description("film-service-update-film-1-updated")
				.build();
		filmUpdate.setId(film.getId());

		try {
			filmService.updateFilm(filmUpdate);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		Film filmFromStorage = null;
		try {
			filmFromStorage = filmService.getFilm(filmUpdate.getId());
		} catch (Exception e) {
			Assertions.fail("Не удалось получить фильм по ID");
		}
		Assertions.assertNotNull(filmFromStorage, "Не удалось получить фильм по ID");
		Assertions.assertEquals(filmUpdate, filmFromStorage, "Полученный фильм не соответствует обновленному");

		final Film film2Update = Film.builder()
				.name("film-service-update-film-1-not-exists")
				.description("film-service-update-film-1-updated")
				.build();
		assertThrows(ValidationException.class, () -> {
			filmService.updateFilm(film2Update);
		}, "Не получено исключение валидации при обновлении без ID");

		film2Update.setId(999L);
		assertThrows(NotFoundException.class, () -> {
			filmService.updateFilm(film2Update);
		}, "Не получено исключение NotFoundException");
	}

	@Test
	void deleteFilm() {
		Film film = Film.builder()
				.name("film-service-delete-film-1")
				.description("film-service-delete-film-1")
				.build();
		try {
			film = filmService.addFilm(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long filmId = film.getId();
		filmService.deleteFilm(filmId);

		assertThrows(NotFoundException.class, () -> {
			filmService.getFilm(filmId);
		}, "Не удалось удалить фильм");
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
		User user2 = User.builder().login("film-service-deletelike-user-2").email("email@email.ru").build();
		Film film = Film.builder().name("film").build();
		try {
			user = userStorage.add(user);
			user2 = userStorage.add(user2);
			film = filmService.addFilm(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		final Long userId = user.getId();
		final Long userId2 = user2.getId();
		final Long filmId = film.getId();

		try {
			filmService.addLike(filmId, userId);
			filmService.addLike(filmId, userId2);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при добавлении лайка к фильму");
		}
		Assertions.assertEquals(2, filmService.getLikes(filmId).size(), "Кол-во лайков не соответствует добавленным");

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
		Assertions.assertEquals(1, filmService.getLikes(filmId).size(), "Кол-во лайков не соответствует добавленным");

		try {
			filmService.deleteFilm(filmId);
		} catch (Exception e) {
			Assertions.fail("Получено исключение при удалении фильма");
		}
		Assertions.assertEquals(0, filmService.getLikes(filmId).size(), "После удаления фильма список лайков к нему не удалился");
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
		List<Film> popularFilms = filmService.getPopularFilms(3, null, null).stream().toList();
		Assertions.assertEquals(3, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film2, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film3, popularFilms.get(1), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film1, popularFilms.get(2), "Последовательность популярности фильмов некорректная");
	}

	@DisplayName("Популярные фильмы с жанром")
	@Test
	void getPopularFilmsWithGenre() {
		filmService.getFilms()
				.forEach(film -> filmStorage.delete(film.getId()));

		User user1 = User.builder().login("film-service-getpopular-genre-user-11").email("email@email.ru").build();
		User user2 = User.builder().login("film-service-getpopular-genre-user-22").email("email@email.ru").build();
		Film film1 = Film.builder().name("film-11").build();
		Film film2 = Film.builder().name("film-22").build();
		Film film3 = Film.builder().name("film-33").build();

		film3.getGenres().add(genreStorage.get(3L));
		film2.getGenres().add(genreStorage.get(2L));
		film1.getGenres().add(genreStorage.get(3L));

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
		 * 2 фильм должен отсортироваться
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
		List<Film> popularFilms = filmService.getPopularFilms(3, 3L, null).stream().toList();
		Assertions.assertEquals(2, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film3, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film1, popularFilms.get(1), "Последовательность популярности фильмов некорректная");
	}

	@DisplayName("Популярные фильмы с годом")
	@Test
	void getPopularFilmsWithYear() {
		filmService.getFilms()
				.forEach(film -> filmStorage.delete(film.getId()));

		User user1 = User.builder().login("film-service-getpopular-year-user-11").email("email@email.ru").build();
		User user2 = User.builder().login("film-service-getpopular-year-user-22").email("email@email.ru").build();
		Film film1 = Film.builder().name("film-11").releaseDate(LocalDate.of(2000, 1, 1)).build();
		Film film2 = Film.builder().name("film-22").releaseDate(LocalDate.of(2010, 1, 1)).build();
		Film film3 = Film.builder().name("film-33").releaseDate(LocalDate.of(2000, 1, 1)).build();

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
		 * 2 фильм должен отсортироваться
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
		List<Film> popularFilms = filmService.getPopularFilms(3, null, 2000).stream().toList();
		Assertions.assertEquals(2, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film3, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film1, popularFilms.get(1), "Последовательность популярности фильмов некорректная");
	}

	@DisplayName("Популярные фильмы с годом и жанром")
	@Test
	void getPopularFilmsWithYearAndGenre() {
		filmService.getFilms()
				.forEach(film -> filmStorage.delete(film.getId()));

		User user1 = User.builder().login("film-service-getpopular-year-genre-user-11").email("email@email.ru").build();
		User user2 = User.builder().login("film-service-getpopular-year-genre-user-22").email("email@email.ru").build();
		Film film1 = Film.builder().name("film-11").releaseDate(LocalDate.of(2000, 1, 1)).build();
		Film film2 = Film.builder().name("film-22").releaseDate(LocalDate.of(2010, 1, 1)).build();
		Film film3 = Film.builder().name("film-33").releaseDate(LocalDate.of(2000, 1, 1)).build();
		Film film4 = Film.builder().name("film-44").releaseDate(LocalDate.of(2010, 1, 1)).build();

		film3.getGenres().add(genreStorage.get(3L));
		film2.getGenres().add(genreStorage.get(2L));
		film1.getGenres().add(genreStorage.get(3L));
		film4.getGenres().add(genreStorage.get(3L));

		try {
			user1 = userStorage.add(user1);
			user2 = userStorage.add(user2);
			film1 = filmService.addFilm(film1);
			film2 = filmService.addFilm(film2);
			film3 = filmService.addFilm(film3);
			film4 = filmService.addFilm(film4);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		/*
		 * 1 фильму - 0 лайков
		 * 2 фильму - 2 лайков
		 * 3 фильму - 1 лайков
		 * 4 фильму - 0 лайков
		 *
		 * */
		try {
			filmService.addLike(film2.getId(), user1.getId());
			filmService.addLike(film2.getId(), user2.getId());
			filmService.addLike(film3.getId(), user2.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		List<Film> popularFilms = filmService.getPopularFilms(3, 3L, 2000).stream().toList();
		Assertions.assertEquals(2, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film3, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
		Assertions.assertEquals(film1, popularFilms.get(1), "Последовательность популярности фильмов некорректная");
		popularFilms = filmService.getPopularFilms(3, 2L, 2010).stream().toList();
		Assertions.assertEquals(1, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film2, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
		popularFilms = filmService.getPopularFilms(3, 3L, 2010).stream().toList();
		Assertions.assertEquals(1, popularFilms.size(), "Длина списка популярных фильмов неверная");
		Assertions.assertEquals(film4, popularFilms.getFirst(), "Последовательность популярности фильмов некорректная");
	}
}
