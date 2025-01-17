package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

	private final Map<Long, Film> films = new HashMap<>();

	@GetMapping
	public Collection<Film> findAll() {
		log.info("Запрос на получение фильмов");
		log.debug("Список фильмов: {}", films);

		return films.values();
	}

	@PostMapping
	public Film create(@Valid @RequestBody Film film) {
		log.info("Запрос на создание фильма");
		log.debug(film.toString());

		film.validate();

		film.setId(getNextId());
		films.put(film.getId(), film);

		log.info("Фильм создан");
		log.debug(film.toString());

		return film;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film newFilm) {
		log.info("Запрос на обновление фильма");
		log.debug(newFilm.toString());

		if (newFilm.getId() == null) {
			log.error("При обновлении фильма Id не указан");
			throw new ValidationException("Id фильма должен быть указан");
		}

		if (films.containsKey(newFilm.getId())) {
			Film oldFilm = films.get(newFilm.getId());
			oldFilm.setName(newFilm.getName());
			oldFilm.setDescription(newFilm.getDescription());
			oldFilm.setReleaseDate(newFilm.getReleaseDate());
			oldFilm.setDuration(newFilm.getDuration());

			log.info("Фильм обновлен");
			log.debug(newFilm.toString());

			return oldFilm;
		}
		throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
	}

	// вспомогательный метод для генерации идентификатора нового поста
	private long getNextId() {
		long currentMaxId = films.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}

	/*private void validate(Film film) {
		if (film.getName() == null || film.getName().isBlank()) {
			throw new ValidationException("Наименование не может быть пустым");
		}
		if (film.getDescription() != null && film.getDescription().length() > 200) {
			throw new ValidationException("Максимальная длина описания — 200 символов");
		}
		if (film.getReleaseDate() != null &&
				film.getReleaseDate().isBefore(Instant.parse("1895-12-28"))) {
			throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
		}
		if (film.getDuration() != null && film.getDuration().toMinutes() > 0) {
			throw new ValidationException("Продолжительность фильма должна быть положительным числом");
		}
	}*/
}
