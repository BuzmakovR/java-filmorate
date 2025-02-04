package ru.yandex.practicum.filmorate.storage.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryFilmStorageTests {

	private InMemoryFilmStorage filmStorage;

	@BeforeEach
	void initStorage() {
		try {
			filmStorage = new InMemoryFilmStorage();
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void addFilm() {
		Film film1 = Film.builder()
				.name("film-1")
				.description("film-1")
				.build();

		try {
			filmStorage.add(film1);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void getFilms() {
		Film film1 = Film.builder()
				.name("film-1")
				.description("film-1")
				.build();
		Film film2 = Film.builder()
				.name("film-2")
				.description("film-2")
				.build();
		try {
			filmStorage.add(film1);
			filmStorage.add(film2);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		List<Film> films = filmStorage.getAll().stream().toList();
		Assertions.assertEquals(2, films.size(), "Кол-во фильмов не соответствует добавленному кол-ву");
		Assertions.assertEquals(film1, films.getFirst(), "Добавленный 1 фильм не соответствует полученному");
		Assertions.assertEquals(film2, films.get(1), "Добавленный 2 фильм не соответствует полученному");

		Optional<Film> optionalFilm2 = filmStorage.get(film2.getId());
		Assertions.assertTrue(optionalFilm2.isPresent(), "Не удалось получить 2 фильм по ID");
		Assertions.assertEquals(film2, optionalFilm2.get(), "Добавленный 2 фильм не соответствует полученному");
	}

	@Test
	void updateFilms() {
		Film film = Film.builder()
				.name("film-1")
				.description("film-1")
				.build();
		try {
			filmStorage.add(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		Film filmUpdate = Film.builder()
				.name("film-1-updated")
				.description("film-1-updated")
				.build();
		filmUpdate.setId(film.getId());

		try {
			filmStorage.update(filmUpdate);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		Optional<Film> filmUpdatedOptional = filmStorage.get(filmUpdate.getId());
		Assertions.assertTrue(filmUpdatedOptional.isPresent(), "Не удалось получить фильм по ID");
		Assertions.assertEquals(filmUpdate, filmUpdatedOptional.get(), "Полученный фильм не соответствует обновленному");

		final Film film2Update = Film.builder()
				.name("film-2-not-exists")
				.description("film-2-not-exists")
				.build();
		assertThrows(ValidationException.class, () -> {
			filmStorage.update(film2Update);
		}, "Не получено исключение валидации при обновлении без ID");

		film2Update.setId(999L);
		assertThrows(NotFoundException.class, () -> {
			filmStorage.update(film2Update);
		}, "Не получено исключение NotFoundException");
	}

	@Test
	void deleteFilms() {
		Film film = Film.builder()
				.name("film-1")
				.description("film-1")
				.build();
		try {
			film = filmStorage.add(film);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		filmStorage.delete(film.getId());
		Optional<Film> filmOptional = filmStorage.get(film.getId());
		assertTrue(filmOptional.isEmpty(), "Не удалось удалить фильм");
	}
}
