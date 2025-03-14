package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.Collection;
import java.util.Set;

public interface FilmLikeStorage {

	Collection<Long> getFilmLikes(Long filmId);

	Set<Long> getUsersWithSameLikes(Long userId);

	void addFilmLike(Long filmId, Long userId);

	void deleteFilmLike(Long filmId, Long userId);

	void deleteAllLikesForFilm(Long filmId);

	Set<FilmLike> getAllLikes();
}
