package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Review {

	private Long reviewId;

	private Long filmId;

	private Long userId;

	@Builder.Default
	private boolean isPositive = true;

	private String content;

	@JsonIgnore
	private Set<ReviewLike> reviewLikes;

	@JsonGetter("useful")
	public int getUseful() {
		int useful = 0;
		for (ReviewLike reviewLike : reviewLikes) {
			useful += (reviewLike.isLike ? 1 : -1);
		}
		return useful;
	}
}
