package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
	Director get(Long id);

	Collection<Director> getAll();

	Director create(Director director);

	Director update(Director newDirector);

	void deleteById(Long id);

}