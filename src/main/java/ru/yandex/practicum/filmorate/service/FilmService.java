package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.enums.FilmSortBy;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

	@Qualifier("filmDbStorage")
	private final FilmStorage filmStorage;

	@Qualifier("filmLikeDbStorage")
	private final FilmLikeStorage filmLikeStorage;

	@Qualifier("userDbStorage")
	private final UserStorage userStorage;

	@Qualifier("genreDbStorage")
	private final GenreStorage genreStorage;

	@Qualifier("mpaRatingDbStorage")
	private final MpaRatingStorage mpaRatingStorage;

	@Qualifier("feedDbStorage")
	private final FeedStorage feedStorage;

	@Qualifier("directorDbStorage")
	private final DirectorStorage directorStorage;

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
		feedStorage.addEvent(userId, filmId, EventOperation.ADD, EventType.LIKE);
	}

	public void deleteLike(Long filmId, Long userId) {
		filmStorage.get(filmId);
		userStorage.get(userId);
		filmLikeStorage.deleteFilmLike(filmId, userId);
		feedStorage.addEvent(userId, filmId, EventOperation.REMOVE, EventType.LIKE);
	}

	public Collection<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
		if (count < 1) {
			throw new ValidationException("Значение переданного параметра количество записей должен быть больше 0");
		}
		return filmStorage.getPopular(count, genreId, year);
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

	public Collection<Film> getFilmsByDirectorSorted(Long directorId, String sortBy) {
		log.info("Запрос фильмов режиссера {} с сортировкой по {}", directorId, sortBy);
		directorStorage.get(directorId);

		FilmSortBy sortEnum = FilmSortBy.fromString(sortBy);
		Collection<Film> films = switch (sortEnum) {
			case YEAR -> filmStorage.getDirectorFilmSortedByYear(directorId);
			case LIKES -> filmStorage.getDirectorFilmSortedByLike(directorId);
		};

		log.debug("Найдено {} фильмов, отсортированных по {}", films.size(), sortBy);
		return films;
	}

	public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
		Collection<Film> films = filmStorage.getCommonFilms(userId, friendId);
		log.debug("Общие фильмы до загрузки жанров: {}", films);

		if (films.isEmpty()) {
			log.warn("Общие фильмы не найдены для пользователей {} и {}", userId, friendId);
			return films;
		}

		Map<Integer, List<Genre>> filmGenresMap = filmStorage.getAllFilmGenres(films);
		log.debug("Загруженные жанры: {}", filmGenresMap);

		films.forEach(film -> {
			Long filmId = film.getId();
			log.debug("Фильм {} до добавления жанров: {}", filmId, film);
			film.setGenres(filmGenresMap.getOrDefault(filmId.intValue(), new ArrayList<>()));
			log.debug("Фильм {} после добавления жанров: {}", filmId, film);
		});

		return films;
	}

	public Collection<Film> searchFilms(String query, String by) {
		log.info("Поиск фильмов по запросу: query={}, by={}", query, by);
		if (query == null || query.isBlank()) {
			throw new ValidationException("Параметр 'query' не может быть пустым");
		}
		if (by == null || by.isBlank()) {
			throw new ValidationException("Параметр 'by' не может быть пустым");
		}

		List<String> searchFields = Arrays.stream(by.split(","))
				.map(String::trim)
				.filter(field -> field.equalsIgnoreCase("title") ||
						field.equalsIgnoreCase("director"))
				.toList();

		if (searchFields.isEmpty()) {
			throw new ValidationException("Параметр 'by' должен содержать 'title' и/или 'director'");
		}
		return filmStorage.searchFilms(query, searchFields);
	}
}
