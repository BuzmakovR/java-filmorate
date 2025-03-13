package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface FilmStorage {

	Collection<Film> getAll();

	Film get(Long id);

	Film add(Film film);

	Film update(Film newFilm);

	Film delete(Long id);

	Collection<Film> getPopular(Integer count, Long genreId, Integer year);

	Map<Integer, List<Genre>> getAllFilmGenres(Collection<Film> films);

	Collection<Film> getCommonFilms(Integer userId, Integer friendId);

	Collection<Film> getDirectorFilmSortedByYear(Long directorId);

	Collection<Film> getDirectorFilmSortedByLike(Long directorId);

	Collection<Film> searchFilms(String query, List<String> searchFields);
}
