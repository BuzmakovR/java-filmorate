package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLike;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.*;

@Qualifier("inMemoryReviewStorage")
public class InMemoryReviewStorage implements ReviewStorage {

    private final Map<Long, Review> reviews = new HashMap<>();

    @Override
    public Review get(Long id) {
        if (!reviews.containsKey(id)) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }
        return reviews.get(id);
    }

    @Override
    public Review add(Review review) {
        review.setReviewId(getNextId());
        reviews.put(review.getReviewId(), review);
        return review;
    }

    @Override
    public Review update(Review newReview) {
        if (newReview.getReviewId() == null) {
            throw new ValidationException("Id отзыва должен быть указан");
        }
        if (!reviews.containsKey(newReview.getReviewId())) {
            throw new NotFoundException("Отзыв с id = " + newReview.getReviewId() + " не найден");
        }
        reviews.put(newReview.getReviewId(), newReview);
        return newReview;
    }

    @Override
    public Review delete(Long id) {
        Optional<Review> optionalReview = Optional.ofNullable(reviews.remove(id));
        if (optionalReview.isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + id + " не найден");
        }

        return optionalReview.get();
    }

    @Override
    public Collection<Review> getReviews(Long filmId, Integer count) {
        count = count == null ? 10 : count;

        if (filmId != null) {
            return reviews.values()
                    .stream()
                    .filter(review -> Objects.equals(filmId, review.getFilmId()))
                    .limit(count)
                    .toList();
        }
        return reviews.values()
                .stream()
                .limit(count)
                .toList();
    }

    @Override
    public void addLike(ReviewLike reviewLike) {
        reviews.get(reviewLike.getReviewId())
                .addReviewLike(reviewLike);
    }

    @Override
    public void deleteLike(ReviewLike reviewLike) {
        reviews.get(reviewLike.getReviewId())
                .deleteReviewLike(reviewLike);
    }

    private long getNextId() {
        long currentMaxId = reviews.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
