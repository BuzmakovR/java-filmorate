package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films = new HashMap<>();

	@Override
	public Collection<Film> getAll() {
		return List.copyOf(films.values());
	}

	@Override
	public Optional<Film> get(Long id) {
		return Optional.ofNullable(films.get(id));
	}

	@Override
	public Film add(Film film) {
		film.validate();

		film.setId(getNextId());
		films.put(film.getId(), film);

		return film;
	}

	@Override
	public Film update(Film newFilm) {
		if (newFilm.getId() == null) {
			throw new ValidationException("Id фильма должен быть указан");
		}

		if (films.containsKey(newFilm.getId())) {
			films.put(newFilm.getId(), newFilm);
			return newFilm;
		}
		throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
	}

	@Override
	public Film delete(Long id) {
		return films.remove(id);
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
