package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("inMemoryFilmLikeStorage")
@RequiredArgsConstructor
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
	private final Set<FilmLike> likes = new HashSet<>();

	@Override
	public Collection<Long> getFilmLikes(Long filmId) {
		return likes.stream()
				.filter(filmLike -> Objects.equals(filmLike.getFilmId(), filmId))
				.map(FilmLike::getUserId)
				.toList();
	}

	@Override
	public Set<Long> getUsersWithSameLikes(Long userId) {
		Set<Long> userFilmIds = likes.stream()
				.filter(filmLike -> Objects.equals(userId, filmLike.getUserId()))
				.map(FilmLike::getFilmId)
				.collect(Collectors.toSet());

		return likes.stream()
				.filter(filmLike -> !Objects.equals(filmLike.getUserId(), userId)
						&& userFilmIds.contains(filmLike.getFilmId()))
				.map(FilmLike::getUserId)
				.collect(Collectors.toSet());
	}

	@Override
	public void addFilmLike(Long filmId, Long userId) {
		likes.add(FilmLike.builder()
				.filmId(filmId)
				.userId(userId)
				.build());
	}

	@Override
	public void deleteFilmLike(Long filmId, Long userId) {
		likes.remove(FilmLike.builder()
				.filmId(filmId)
				.userId(userId)
				.build());
	}

	@Override
	public Set<FilmLike> getAllLikes() {
		return Set.copyOf(likes);
	}

	@Override
	public void deleteAllLikesForFilm(Long filmId) {
		likes.stream()
				.filter(filmLike -> Objects.equals(filmLike.getFilmId(), filmId))
				.toList()
				.forEach(likes::remove);
	}
}
