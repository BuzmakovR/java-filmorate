package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.MPARatingStorage;

import java.util.Collection;

@Service
public class MPARatingService {

	@Autowired
	@Qualifier("mpaRatingDbStorage")
	private final MPARatingStorage mpaRatingStorage;

	@Autowired
	public MPARatingService(@Qualifier("mpaRatingDbStorage") MPARatingStorage mpaRatingStorage) {
		this.mpaRatingStorage = mpaRatingStorage;
	}

	public Collection<MPARating> getRatings() {
		return mpaRatingStorage.getAll();
	}

	public MPARating getRating(final Long ratingId) {
		return mpaRatingStorage.get(ratingId);
	}

	public MPARating addRating(MPARating rating) {
		return mpaRatingStorage.add(rating);
	}

	public MPARating deleteRating(final Long ratingId) {
		return mpaRatingStorage.delete(ratingId);
	}
}
