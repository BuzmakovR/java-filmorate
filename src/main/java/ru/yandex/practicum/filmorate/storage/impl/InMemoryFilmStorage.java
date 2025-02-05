package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films = new HashMap<>();

	@Override
	public Collection<Film> getAll() {
		return List.copyOf(films.values());
	}

	@Override
	public Film get(Long id) {
		if (!films.containsKey(id)) {
			throw new NotFoundException("Фильм с id = " + id + " не найден");
		}
		return films.get(id);
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

		Film currentFilm = get(newFilm.getId());
		for (Long userId : currentFilm.getUserLikes()) {
			newFilm.addUserLike(userId);
		}
		films.put(newFilm.getId(), newFilm);
		return newFilm;
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
