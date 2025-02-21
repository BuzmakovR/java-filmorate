package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.Collection;

public interface MPARatingStorage {

	Collection<MPARating> getAll();

	MPARating get(Long id);

	MPARating add(MPARating mpaRating);

	MPARating delete(Long id);
}
