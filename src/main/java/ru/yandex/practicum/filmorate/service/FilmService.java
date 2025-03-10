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

    @Autowired
    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("filmLikeDbStorage") FilmLikeStorage filmLikeStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("mpaRatingDbStorage") MpaRatingStorage mpaRatingStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaRatingStorage = mpaRatingStorage;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> getFilms() {
        log.info("Запрос на получение всех фильмов");
        Collection<Film> films = filmStorage.getAll();
        log.debug("Получено {} фильмов", films.size());
        return films;
    }

    public Film getFilm(final Long filmId) {
        log.info("Запрос на получение фильма с ID {}", filmId);
        Film film = filmStorage.get(filmId);
        log.debug("Найден фильм: {}", film);
        return film;
    }

    public Film addFilm(Film film) {
        log.info("Добавление нового фильма: {}", film);
        prepareAndValidateFilm(film);
        Film addedFilm = filmStorage.add(film);
        log.info("Фильм успешно добавлен с ID {}", addedFilm.getId());
        return addedFilm;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Обновление фильма с ID {}", newFilm.getId());
        if (newFilm.getId() == null) {
            log.error("Попытка обновить фильм без указания ID");
            throw new ValidationException("Id фильма должен быть указан");
        }
        prepareAndValidateFilm(newFilm);
        Film updatedFilm = filmStorage.update(newFilm);
        log.info("Фильм с ID {} успешно обновлен", updatedFilm.getId());
        return updatedFilm;
    }

    public Film deleteFilm(final Long filmId) {
        log.info("Удаление фильма с ID {}", filmId);
        Film film = filmStorage.delete(filmId);
        filmLikeStorage.deleteAllLikesForFilm(film.getId());
        log.info("Фильм с ID {} успешно удален", filmId);
        return film;
    }

    public Collection<Long> getLikes(Long filmId) {
        log.debug("Запрос лайков для фильма с ID {}", filmId);
        return filmLikeStorage.getFilmLikes(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        filmStorage.get(filmId);
        userStorage.get(userId);
        filmLikeStorage.addFilmLike(filmId, userId);
        log.debug("Лайк пользователя {} фильму {} добавлен", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Пользователь {} удаляет лайк с фильма {}", userId, filmId);
        filmStorage.get(filmId);
        userStorage.get(userId);
        filmLikeStorage.deleteFilmLike(filmId, userId);
        log.debug("Лайк пользователя {} с фильма {} удален", userId, filmId);
    }

    public Collection<Film> getPopularFilms(Integer count, Long genreId, Integer year) {
        log.info("Запрос популярных фильмов. count={}, genreId={}, year={}", count, genreId, year);
        if (count < 1) {
            log.error("Некорректное значение count: {}", count);
            throw new ValidationException("Значение переданного параметра количество записей должен быть больше 0");
        }
        Collection<Film> films = filmStorage.getPopular(count, genreId, year);
        log.debug("Найдено {} популярных фильмов", films.size());
        return films;
    }

    private void prepareAndValidateFilm(Film film) {
        log.debug("Подготовка и валидация фильма: {}", film);
        try {
            Optional.ofNullable(film.getMpa()).ifPresent(mpa -> film.setMpa(mpaRatingStorage.get(mpa.getId())));
            Optional.ofNullable(film.getGenres())
                    .ifPresent(genres -> {
                        List<Genre> inputGenre = new ArrayList<>(genres.stream().distinct().toList());
                        film.clearGenre();
                        inputGenre.forEach(genre -> film.addGenre(genreStorage.get(genre.getId())));
                    });
            validateFilmDirector(film);
            film.validate();
            log.debug("Фильм успешно подготовлен: {}", film);
        } catch (ValidationException e) {
            log.error("Ошибка валидации фильма: {}", e.getMessage());
            throw e;
        }
    }

    public List<Film> getFilmsByDirectorSorted(Long directorId, String sortBy) {
        log.info("Запрос фильмов режиссера {} с сортировкой по {}", directorId, sortBy);
        List<Film> films;
        if ("year".equalsIgnoreCase(sortBy)) {
            films = filmStorage.getDirectorFilmSortedByYear(directorId);
            log.debug("Найдено {} фильмов, отсортированных по году", films.size());
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            films = filmStorage.getDirectorFilmSortedByLike(directorId);
            log.debug("Найдено {} фильмов, отсортированных по лайкам", films.size());
        } else {
            log.error("Некорректный параметр сортировки: {}", sortBy);
            throw new IllegalArgumentException("Invalid sortBy parameter");
        }
        setAdditionalFieldsForFilms(films);
        return films;
    }

    private void setAdditionalFieldsForFilms(List<Film> films) {
        log.debug("Установка дополнительных полей для {} фильмов", films.size());
        setDirectorsForFilms(films);
    }

    private void setDirectorsForFilms(List<Film> films) {
        log.trace("Установка режиссеров для фильмов");
        Map<Long, Set<Director>> filmsDirectors = directorStorage.getAllFilmsDirectors();
        for (Film film : films) {
            Set<Director> directors = filmsDirectors.getOrDefault(film.getId(), new HashSet<>());
            film.setDirectors(directors);
        }
    }

    private void validateFilmDirector(Film film) {
        log.debug("Валидация режиссеров фильма {}", film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            Set<Long> availableDirectorIds = directorStorage.getAll().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());

            Set<Long> filmDirectorsIds = film.getDirectors().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());

            if (!availableDirectorIds.containsAll(filmDirectorsIds)) {
                log.error("Найдены несуществующие ID режиссеров: {}", filmDirectorsIds);
                throw new ValidationException("Invalid film director!");
            }
        }
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
}
