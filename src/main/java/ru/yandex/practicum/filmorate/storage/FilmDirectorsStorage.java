package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmDirectorsStorage {
	void addFilmDirectors(Film film);

	void updateFilmDirectors(Film film);
}
