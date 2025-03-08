package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    Optional<Director> getById(Long id);

    List<Director> getAll();

    Director create(Director director);

    Director update(Director newDirector);

    void deleteById(Long id);

    Map<Long, Set<Director>> getAllFilmsDirectors();

    void saveDirectors(Film film);

    void updateDirectors(Film film);
}
