package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Review {

	private Long reviewId;

	@NonNull
	private Long filmId;

	@NonNull
	private Long userId;

	@NonNull
	@JsonProperty("isPositive")
	private Boolean isPositive;

	@NotBlank
	private String content;

	@JsonIgnore
	private final Set<ReviewLike> reviewLikes = new HashSet<>();

	public void addReviewLike(ReviewLike reviewLike) {
		reviewLikes.remove(reviewLike);
		reviewLikes.add(reviewLike);
	}

	public void deleteReviewLike(ReviewLike reviewLike) {
		reviewLikes.remove(reviewLike);
	}

	@JsonGetter("useful")
	public int getUseful() {
		int useful = 0;
		for (ReviewLike reviewLike : reviewLikes) {
			useful += (reviewLike.isLike ? 1 : -1);
		}
		return useful;
	}
}
