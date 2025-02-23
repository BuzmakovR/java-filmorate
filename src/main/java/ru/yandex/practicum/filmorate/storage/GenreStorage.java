package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {

	Collection<Genre> getAll();

	Genre get(Long id);

	Genre add(Genre genre);

	Genre delete(Long id);
}
