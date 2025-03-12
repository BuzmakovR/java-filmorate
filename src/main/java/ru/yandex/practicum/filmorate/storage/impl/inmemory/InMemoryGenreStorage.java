package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Component("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Long, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1L, Genre.builder().id(1L).name("Комедия").build());
        genres.put(2L, Genre.builder().id(2L).name("Драма").build());
        genres.put(3L, Genre.builder().id(3L).name("Мультфильм").build());
        genres.put(4L, Genre.builder().id(4L).name("Триллер").build());
        genres.put(5L, Genre.builder().id(5L).name("Документальный").build());
        genres.put(6L, Genre.builder().id(6L).name("Боевик").build());
    }

    @Override
    public Collection<Genre> getAll() {
        return List.copyOf(genres.values());
    }

    @Override
    public Genre get(Long id) {
        if (!genres.containsKey(id)) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
        return genres.get(id);
    }

    @Override
    public Genre add(Genre genre) {
        genre.setId(getNextId());
        genres.put(genre.getId(), genre);
        return genre;
    }

    @Override
    public Genre delete(Long id) {
        Optional<Genre> optionalGenre = Optional.ofNullable(genres.remove(id));
        return optionalGenre.orElse(null);
    }

    private long getNextId() {
        long currentMaxId = genres.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
