package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService {

	@Autowired
	@Qualifier("filmDbStorage")
	private final FilmStorage filmStorage;

	@Autowired
	@Qualifier("filmLikeDbStorage")
	private final FilmLikeStorage filmLikeStorage;

	@Autowired
	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	@Autowired
	@Qualifier("genreDbStorage")
	private final GenreStorage genreStorage;

	@Autowired
	@Qualifier("mpaRatingDbStorage")
	private final MPARatingStorage mpaRatingStorage;

	public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
					   @Qualifier("filmLikeDbStorage") FilmLikeStorage filmLikeStorage,
					   @Qualifier("userDbStorage") UserStorage userStorage,
					   @Qualifier("genreDbStorage") GenreStorage genreStorage,
					   @Qualifier("mpaRatingDbStorage") MPARatingStorage mpaRatingStorage) {
		this.filmStorage = filmStorage;
		this.filmLikeStorage = filmLikeStorage;
		this.userStorage = userStorage;
		this.genreStorage = genreStorage;
		this.mpaRatingStorage = mpaRatingStorage;
	}

	public Collection<Film> getFilms() {
		return filmStorage.getAll();
	}

	public Film getFilm(final Long filmId) {
		return filmStorage.get(filmId);
	}

	public Film addFilm(Film film) {
		validateFilm(film);
		return filmStorage.add(film);
	}

	public Film updateFilm(Film newFilm) {
		if (newFilm.getId() == null) {
			throw new ValidationException("Id фильма должен быть указан");
		}
		validateFilm(newFilm);
		return filmStorage.update(newFilm);
	}

	public Film deleteFilm(final Long filmId) {
		return filmStorage.delete(filmId);
	}

	public void addLike(Long filmId, Long userId) {
		filmStorage.get(filmId);
		userStorage.get(userId);
		filmLikeStorage.addFilmLike(filmId, userId);
	}

	public void deleteLike(Long filmId, Long userId) {
		filmStorage.get(filmId);
		userStorage.get(userId);
		filmLikeStorage.deleteFilmLike(filmId, userId);
	}

	public Collection<Film> getPopularFilms(Integer count) {
		if (count < 1) {
			throw new ValidationException("Значение переданного параметра количество записей должен быть больше 0");
		}
		return filmStorage.getPopular(count);
//				filmStorage.getAll()
//				.stream()
//				.sorted(Collections.reverseOrder(Comparator.comparing(film -> film.getUserLikes().size())))
//				.limit(count)
//				.toList();
	}

	private void validateFilm(Film film) {
		film.validate();
		Optional.ofNullable(film.getMpa())
				.ifPresent(mpa -> mpaRatingStorage.get(mpa.getId()));
		Optional.ofNullable(film.getGenres())
				.ifPresent(genres -> {
					genres.forEach(
							genre -> {
								genreStorage.get(genre.getId());
							}
					);
				});
	}

}
