package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

	Collection<Film> getAll();

	Optional<Film> get(Long id);

	Film add(Film film);

	Film update(Film newFilm);

	Film delete(Long id);
}
