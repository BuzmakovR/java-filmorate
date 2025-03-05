package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmLikeStorage {

	Collection<Long> getFilmLikes(Long filmId);

	void addFilmLike(Long filmId, Long userId);

	void deleteFilmLike(Long filmId, Long userId);

	void deleteAllLikesForFilm(Long filmId);

	Map<Long, Set<Long>> getAllLikes();
}
