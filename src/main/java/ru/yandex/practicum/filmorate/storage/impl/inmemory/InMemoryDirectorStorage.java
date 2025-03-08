package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class InMemoryDirectorStorage implements DirectorStorage {
    @Override
    public Optional<Director> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Director> getAll() {
        return List.of();
    }

    @Override
    public Director create(Director director) {
        return null;
    }

    @Override
    public Director update(Director newDirector) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Map<Long, Set<Director>> getAllFilmsDirectors() {
        return Map.of();
    }

    @Override
    public void saveDirectors(Film film) {

    }

    @Override
    public void updateDirectors(Film film) {

    }
}
