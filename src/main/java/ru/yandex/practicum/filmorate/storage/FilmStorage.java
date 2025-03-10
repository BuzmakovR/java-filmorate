package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

	Collection<Film> getAll();

	Film get(Long id);

	Film add(Film film);

	Film update(Film newFilm);

	Film delete(Long id);

	Collection<Film> getPopular(Integer count, Long genreId, Integer year);
	Collection<Film> getPopular(Integer id);

	List<Film> getDirectorFilmSortedByYear(Long directorId);

	List<Film> getDirectorFilmSortedByLike(Long directorId);
}
