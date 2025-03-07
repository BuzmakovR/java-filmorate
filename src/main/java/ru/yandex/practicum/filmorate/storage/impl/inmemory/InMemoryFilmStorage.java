package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films = new HashMap<>();

	@Autowired
	private final InMemoryFilmLikeStorage inMemoryFilmLikeStorage;

	public InMemoryFilmStorage(InMemoryFilmLikeStorage inMemoryFilmLikeStorage) {
		this.inMemoryFilmLikeStorage = inMemoryFilmLikeStorage;
	}

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
		film.setId(getNextId());
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public Film update(Film newFilm) {
		if (newFilm.getId() == null) {
			throw new ValidationException("Id фильма должен быть указан");
		}
		if (!films.containsKey(newFilm.getId())) {
			throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
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

	@Override
	public Collection<Film> getPopular(Integer count) {
		return films.values()
				.stream()
				.sorted(Collections.reverseOrder(
						Comparator.comparing(film -> inMemoryFilmLikeStorage.getFilmLikes(film.getId()).size())))
				.limit(count)
				.toList();
	}
}
