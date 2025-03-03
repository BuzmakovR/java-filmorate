package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("inMemoryMpaRatingDbStorage")
public class InMemoryMpaRatingStorage implements MpaRatingStorage {

	private final Map<Long, MpaRating> ratings = new HashMap<>();

	public InMemoryMpaRatingStorage() {
		ratings.put(1L, MpaRating.builder().id(1L).name("G").build());
		ratings.put(2L, MpaRating.builder().id(2L).name("PG").build());
		ratings.put(3L, MpaRating.builder().id(3L).name("PG-13").build());
		ratings.put(4L, MpaRating.builder().id(4L).name("R").build());
		ratings.put(5L, MpaRating.builder().id(5L).name("NC-17").build());
	}

	@Override
	public Collection<MpaRating> getAll() {
		return List.copyOf(ratings.values());
	}

	@Override
	public MpaRating get(Long id) {
		if (!ratings.containsKey(id)) {
			throw new NotFoundException("Рейтинг с id = " + id + " не найден");
		}
		return ratings.get(id);
	}

	@Override
	public MpaRating add(MpaRating rating) {
		rating.setId(getNextId());
		ratings.put(rating.getId(), rating);
		return rating;
	}

	@Override
	public MpaRating delete(Long id) {
		Optional<MpaRating> optionalRating = Optional.ofNullable(ratings.remove(id));
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
