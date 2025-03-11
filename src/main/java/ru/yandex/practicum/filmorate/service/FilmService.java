package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Long, Set<Film>> getFilmLikesData() {
        log.info("Запрос данных о лайках фильмов");
        Map<Long, Film> films = filmStorage.getAll().stream()
                .collect(Collectors.toMap(Film::getId, film -> film));
        log.debug("Получено {} фильмов для обработки лайков", films.size());

        Map<Long, Set<Long>> likesByFilm = filmLikeStorage.getAllLikes();
        log.debug("Получено {} записей лайков", likesByFilm.size());

        Map<Long, Set<Film>> filmLikesData = likesByFilm.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(films::get)
                                .collect(Collectors.toSet())
                ));
        log.info("Сформированы данные о лайках для {} фильмов", filmLikesData.size());
        return filmLikesData;
    }

    public Collection<Film> getFilmsByDirectorSorted(Long directorId, String sortBy) {
        log.info("Запрос фильмов режиссера {} с сортировкой по {}", directorId, sortBy);
        validateSortBy(sortBy);

        Collection<Film> films = "year".equalsIgnoreCase(sortBy)
                ? filmStorage.getDirectorFilmSortedByYear(directorId)
                : filmStorage.getDirectorFilmSortedByLike(directorId);

        log.debug("Найдено {} фильмов, отсортированных по {}", films.size(), sortBy);
        setAdditionalFieldsForFilms(films);
        return films;
    }

    private void validateSortBy(String sortBy) {
        if (!"year".equalsIgnoreCase(sortBy) && !"likes".equalsIgnoreCase(sortBy)) {
            log.error("Некорректный параметр сортировки: {}", sortBy);
            throw new IllegalArgumentException("Invalid sortBy parameter");
        }
    }


    private void setAdditionalFieldsForFilms(Collection<Film> films) {
        log.debug("Установка дополнительных полей для {} фильмов", films.size());
        setDirectorsForFilms(films);
    }

    private void setDirectorsForFilms(Collection<Film> films) {
        log.trace("Установка режиссеров для фильмов");
        Map<Long, Set<Director>> filmsDirectors = directorStorage.getAllFilmsDirectors();
        for (Film film : films) {
            Set<Director> directors = filmsDirectors.getOrDefault(film.getId(), new HashSet<>());
            film.setDirectors(directors);
        }
    }
}
