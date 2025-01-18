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
			films.put(newFilm.getId(), newFilm);

			log.info("Фильм обновлен");
			log.debug(newFilm.toString());

			return newFilm;
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

}
