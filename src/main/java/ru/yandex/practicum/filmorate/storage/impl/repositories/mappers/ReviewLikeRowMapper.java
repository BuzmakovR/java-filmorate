package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewLikeRowMapper implements RowMapper<ReviewLike> {

	@Override
	public ReviewLike mapRow(ResultSet rs, int rowNum) throws SQLException {
		return ReviewLike.builder()
				.reviewId(rs.getLong("review_id"))
				.userId(rs.getLong("user_id"))
				.isLike(rs.getBoolean("is_like"))
				.build();
	}
}
