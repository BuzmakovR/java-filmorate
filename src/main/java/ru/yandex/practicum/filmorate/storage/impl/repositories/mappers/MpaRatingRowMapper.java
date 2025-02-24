package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingRowMapper implements RowMapper<MpaRating> {

	@Override
	public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
		return MpaRating.builder()
				.id(rs.getLong("id"))
				.name(rs.getString("name"))
				.build();
	}
}
