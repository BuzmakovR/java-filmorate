package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
	private final MpaRatingStorage mpaRatingStorage;

	public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
					   @Qualifier("filmLikeDbStorage") FilmLikeStorage filmLikeStorage,
					   @Qualifier("userDbStorage") UserStorage userStorage,
					   @Qualifier("genreDbStorage") GenreStorage genreStorage,
					   @Qualifier("mpaRatingDbStorage") MpaRatingStorage mpaRatingStorage) {
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
		prepareAndValidateFilm(film);
		return filmStorage.add(film);
	}

	public Film updateFilm(Film newFilm) {
		if (newFilm.getId() == null) {
			throw new ValidationException("Id фильма должен быть указан");
		}
		prepareAndValidateFilm(newFilm);
		return filmStorage.update(newFilm);
	}

	public Film deleteFilm(final Long filmId) {
		Film film = filmStorage.delete(filmId);
		filmLikeStorage.deleteAllLikesForFilm(film.getId());
		return film;
	}

	public Collection<Long> getLikes(Long filmId) {
		return filmLikeStorage.getFilmLikes(filmId);
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
	}

	private void prepareAndValidateFilm(Film film) {
		Optional.ofNullable(film.getMpa()).ifPresent(mpa -> film.setMpa(mpaRatingStorage.get(mpa.getId())));
		Optional.ofNullable(film.getGenres())
				.ifPresent(genres -> {
					ArrayList<Genre> inputGenre = new ArrayList<>(genres.stream().distinct().toList());
					film.clearGenre();
					inputGenre.forEach(genre -> film.addGenre(genreStorage.get(genre.getId())));
				});
		film.validate();
	}

	public Map<Long, Set<Film>> getFilmLikesData() {
		log.info("зашли в getFilmLikesData");
		Map<Long, Film> films = filmStorage.getAll().stream()
				.collect(Collectors.toMap(Film::getId, film -> film));
		log.info("успешно запросили пользователей и фильмы");

		Map<Long, Set<Long>> likesByFilm = filmLikeStorage.getAllLikes();
		log.info("успешно запросили лайки пользователей {}", likesByFilm);

		Map<Long, Set<Film>> filmLikesData = new HashMap<>(likesByFilm.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> entry.getValue().stream()
								.map(films::get)
								.collect(Collectors.toSet())
				)));
		log.info("filmLikesData: {}", filmLikesData);
		return filmLikesData;
	}
}
