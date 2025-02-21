package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.MPARatingStorage;

import java.util.*;

@Component("inMemoryMPARatingDbStorage")
public class InMemoryMPARatingDbStorage implements MPARatingStorage {

	private final Map<Long, MPARating> ratings = new HashMap<>();

	public InMemoryMPARatingDbStorage() {
		ratings.put(1L, MPARating.builder().id(1L).name("G").build());
		ratings.put(2L, MPARating.builder().id(2L).name("PG").build());
		ratings.put(3L, MPARating.builder().id(3L).name("PG-13").build());
		ratings.put(4L, MPARating.builder().id(4L).name("R").build());
		ratings.put(5L, MPARating.builder().id(5L).name("NC-17").build());
	}

	@Override
	public Collection<MPARating> getAll() {
		return List.copyOf(ratings.values());
	}

	@Override
	public MPARating get(Long id) {
		if (!ratings.containsKey(id)) {
			throw new NotFoundException("Рейтинг с id = " + id + " не найден");
		}
		return ratings.get(id);
	}

	@Override
	public MPARating add(MPARating rating) {
		rating.setId(getNextId());
		ratings.put(rating.getId(), rating);
		return rating;
	}

	@Override
	public MPARating delete(Long id) {
		Optional<MPARating> optionalRating = Optional.ofNullable(ratings.remove(id));
		return optionalRating.orElse(null);
	}

	private long getNextId() {
		long currentMaxId = ratings.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}

}
