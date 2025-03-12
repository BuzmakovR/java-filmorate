package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private final FilmService filmService;

    //region FILM
    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение фильмов");

        Collection<Film> filmCollection = filmService.getFilms();

        log.debug("Список фильмов: {}", filmCollection);

        return filmCollection;
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable("id") long id) {
        log.info("Запрос на получение фильма с ID: {}", id);

        Film film = filmService.getFilm(id);

        log.debug("Полученный фильм: {}", film);

        return film;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма");
        log.debug(film.toString());

        Film createdFilm = filmService.addFilm(film);

        log.info("Фильм создан c id {}", createdFilm.getId());
        log.debug(createdFilm.toString());

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Запрос на обновление фильма");
        log.debug(newFilm.toString());

        Film updatedFilm = filmService.updateFilm(newFilm);

        log.info("Фильм обновлен");
        log.debug(updatedFilm.toString());

        return updatedFilm;
    }
    //endregion

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable("id") long filmId) {
        return filmService.deleteFilm(filmId);
    }

    //region FILM-LIKE
    @GetMapping("/popular")
    public Collection<Film> popular(@RequestParam(name = "count", defaultValue = "10") int count,
                                    @RequestParam(name = "genreId", required = false) Long genreId,
                                    @RequestParam(name = "year", required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        log.info("Пользователь с id {} лайкает фильм {}", userId, filmId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long filmId, @PathVariable("userId") long userId) {
        filmService.deleteLike(filmId, userId);
    }
    //endregion

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.debug("Просмотр всех общих фильмов");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(
            @PathVariable Long directorId,
            @RequestParam(defaultValue = "likes") String sortBy) {

        return filmService.getFilmsByDirectorSorted(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@RequestParam String query,
                                        @RequestParam(defaultValue = "title") String by) {
        log.info("Запрос на поиск фильмов: query={}, by={}", query, by);
        Collection<Film> films = filmService.searchFilms(query, by);
        log.debug("Найденные фильмы: {}", films);
        return films;
    }
}
