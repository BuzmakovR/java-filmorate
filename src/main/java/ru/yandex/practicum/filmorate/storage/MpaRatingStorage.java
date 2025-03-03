package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingStorage {

	Collection<MpaRating> getAll();

	MpaRating get(Long id);

	MpaRating add(MpaRating mpaRating);

	MpaRating delete(Long id);
}
