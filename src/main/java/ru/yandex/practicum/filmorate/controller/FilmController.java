package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

	@Autowired
	private final FilmService filmService;

	//region FILM
	@GetMapping
	public Collection<Film> findAll() {
		log.info("Запрос на получение фильмов");

		Collection<Film> filmCollection = filmService.getFilms();

		log.debug("Список фильмов: {}", filmCollection);

		return filmCollection;
	}

	@GetMapping("/{id}")
	public Film get(@PathVariable("id") long id) {
		log.info("Запрос на получение фильма с ID: {}", id);

		Film film = filmService.getFilm(id);

		log.debug("Полученный фильм: {}", film);

		return film;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Film create(@Valid @RequestBody Film film) {
		log.info("Запрос на создание фильма");
		log.debug(film.toString());

		Film createdFilm = filmService.addFilm(film);

		log.info("Фильм создан");
		log.debug(createdFilm.toString());

		return film;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film newFilm) {
		log.info("Запрос на обновление фильма");
		log.debug(newFilm.toString());

		Film updatedFilm = filmService.updateFilm(newFilm);

		log.info("Фильм обновлен");
		log.debug(updatedFilm.toString());

		return updatedFilm;
	}
	//endregion

	@DeleteMapping("/{id}")
	public Film delete(@PathVariable("id") long filmId) {
		return filmService.deleteFilm(filmId);
	}

	//region FILM-LIKE
	@GetMapping("/popular")
	public Collection<Film> friends(@RequestParam(name = "count", defaultValue = "10") int count) {
		return filmService.getPopularFilms(count);
	}

	@PutMapping("/{id}/like/{userId}")
	public void addLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
		filmService.addLike(filmId, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public void deleteLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
		filmService.deleteLike(filmId, userId);
	}
	//endregion
}
