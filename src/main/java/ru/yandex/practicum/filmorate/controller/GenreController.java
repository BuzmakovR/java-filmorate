package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Запрос на получение жанров");

        Collection<Genre> users = genreService.getGenres();

        log.debug("Список жанров: {}", users);

        return users;
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable("id") long id) {
        log.info("Запрос на получение пользователя с ID: {}", id);

        Genre genre = genreService.getGenre(id);
        log.debug("Полученный жанр: {}", genre);

        return genre;
    }
}
