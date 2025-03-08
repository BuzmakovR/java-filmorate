package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.util.Collection;

@Repository("reviewLikeDbStorage")
public class ReviewLikeDbStorage extends BaseRepository<ReviewLike> {

	private static final String FIND_LIKES_BY_ID = "SELECT * FROM reviews_likes WHERE review_id = ?";
	private static final String MERGE_QUERY = """
			MERGE INTO reviews_likes t
			USING (
				SELECT CAST(? AS bigint) AS review_id, CAST(? AS bigint) AS user_id, CAST(? AS boolean) AS is_like FROM dual
			) d ON (d.review_id = t.review_id AND d.user_id = t.user_id)
			WHEN NOT MATCHED THEN INSERT(review_id, user_id, is_like) VALUES(d.review_id, d.user_id, d.is_like)
			WHEN MATCHED THEN UPDATE SET t.is_like = d.is_like
			""";
	private static final String DELETE_LIKE = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ? AND is_like = ?";

	public ReviewLikeDbStorage(JdbcTemplate jdbc, RowMapper<ReviewLike> mapper) {
		super(jdbc, mapper);
	}

	public Collection<ReviewLike> getReviewLikes(Long reviewId) {
		return findMany(FIND_LIKES_BY_ID, reviewId);
	}

	public void addLike(ReviewLike reviewLike) {
		update(
				MERGE_QUERY,
				reviewLike.getReviewId(),
				reviewLike.getUserId(),
				reviewLike.isLike()
		);
	}

	public void deleteLike(ReviewLike reviewLike) {
		delete(
				DELETE_LIKE,
				reviewLike.getReviewId(),
				reviewLike.getUserId(),
				reviewLike.isLike()
		);
	}
}
