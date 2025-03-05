package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Collection;

public interface ReviewLikeStorage {

	Collection<ReviewLike> getReviewLikes(Long reviewId);

	void add(ReviewLike reviewLike);

	void delete(ReviewLike reviewLike);
}
