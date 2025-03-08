package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.repositories.DirectorStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
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

	@Autowired
	@Qualifier("directorStorage")
	private final DirectorStorage directorStorage;

	public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
					   @Qualifier("filmLikeDbStorage") FilmLikeStorage filmLikeStorage,
					   @Qualifier("userDbStorage") UserStorage userStorage,
					   @Qualifier("genreDbStorage") GenreStorage genreStorage,
					   @Qualifier("mpaRatingDbStorage") MpaRatingStorage mpaRatingStorage,
					   @Qualifier("directorStorage") DirectorStorage directorStorage) {
		this.filmStorage = filmStorage;
		this.filmLikeStorage = filmLikeStorage;
		this.userStorage = userStorage;
		this.genreStorage = genreStorage;
		this.mpaRatingStorage = mpaRatingStorage;
		this.directorStorage = directorStorage;
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
	}

	private void prepareAndValidateFilm(Film film) {
		Optional.ofNullable(film.getMpa()).ifPresent(mpa -> film.setMpa(mpaRatingStorage.get(mpa.getId())));
		Optional.ofNullable(film.getGenres())
				.ifPresent(genres -> {
					ArrayList<Genre> inputGenre = new ArrayList<>(genres.stream().distinct().toList());
					film.clearGenre();
					inputGenre.forEach(genre -> film.addGenre(genreStorage.get(genre.getId())));
				});
		validateFilmDirector(film);
		film.validate();
	}

	public List<Film> getFilmsByDirectorSorted(Long directorId, String sortBy) {
		List<Film> films;
		if ("year".equalsIgnoreCase(sortBy)) {
			log.info("запустили getDirectorFilmSortedByYear");
			films = filmStorage.getDirectorFilmSortedByYear(directorId);
		} else if ("likes".equalsIgnoreCase(sortBy)) {
			log.info("запустили getDirectorFilmSortedByLike");
			films = filmStorage.getDirectorFilmSortedByLike(directorId);
		} else {
			throw new IllegalArgumentException("Invalid sortBy parameter");
		}
		setAdditionalFieldsForFilms(films);
		return films;
	}

	private void setAdditionalFieldsForFilms(List<Film> films) {
		setDirectorsForFilms(films);
	}
	/**
	 * Set directors for films.
	 *
	 * @param films
	 */
	private void setDirectorsForFilms(List<Film> films) {
		Map<Long, Set<Director>> filmsDirectors = directorStorage.getAllFilmsDirectors();
		for (Film film : films) {
			Set<Director> directors = filmsDirectors.getOrDefault(film.getId(), new HashSet<>());
			film.setDirectors(directors);
		}
	}

	private void validateFilmDirector(Film film) {
		// Validate directors
		if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
			Set<Long> availableDirectorIds = directorStorage.getAll().stream()
					.map(Director::getId)
					.collect(Collectors.toSet());

			Set<Long> filmDirectorsIds = film.getDirectors().stream()
					.map(Director::getId)
					.collect(Collectors.toSet());

			if (!availableDirectorIds.containsAll(filmDirectorsIds)) {
				throw new ValidationException("Invalid film director!");
			}
		}
	}

}
