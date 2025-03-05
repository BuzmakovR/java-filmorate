package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

	Review get(Long id);

	Review add(Review review);

	Review update(Review review);

	Review delete(Long id);

	Collection<Review> getReviewsForFilm(Long filmId, int count);
}
