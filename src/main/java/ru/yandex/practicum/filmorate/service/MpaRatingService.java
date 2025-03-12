package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;

@Service
public class MpaRatingService {

    @Autowired
    @Qualifier("mpaRatingDbStorage")
    private final MpaRatingStorage mpaRatingStorage;

    @Autowired
    public MpaRatingService(@Qualifier("mpaRatingDbStorage") MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public Collection<MpaRating> getRatings() {
        return mpaRatingStorage.getAll();
    }

    public MpaRating getRating(final Long ratingId) {
        return mpaRatingStorage.get(ratingId);
    }

    public MpaRating addRating(MpaRating rating) {
        return mpaRatingStorage.add(rating);
    }

    public MpaRating deleteRating(final Long ratingId) {
        return mpaRatingStorage.delete(ratingId);
    }
}
