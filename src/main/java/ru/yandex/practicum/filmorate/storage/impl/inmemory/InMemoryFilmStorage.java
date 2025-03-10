package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;


@Component("inMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films = new HashMap<>();

	private final InMemoryFilmLikeStorage inMemoryFilmLikeStorage;

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
		Optional<Film> optionalFilm = Optional.ofNullable(films.remove(id));
		if (optionalFilm.isEmpty()) {
			throw new NotFoundException("Фильм с id = " + id + " не найден");
		}

		return optionalFilm.get();
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
	public Collection<Film> getPopular(Integer count, Long genreId, Integer year) {
		return films.values()
				.stream()
				.filter(f -> (genreId == null || f.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId))))
				.filter(f -> (year == null || f.getReleaseDate().getYear() == year))
				.sorted(Collections.reverseOrder(
						Comparator.comparing(film -> inMemoryFilmLikeStorage.getFilmLikes(film.getId()).size())))
				.limit(count)
				.toList();
	}

	@Override
	public Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films) {
		return Map.of();
	}

	@Override
	public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
		return List.of();
	}
}
