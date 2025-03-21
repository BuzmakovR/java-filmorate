package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class DirectorRowMapper implements RowMapper<Director> {
	@Override
	public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Director.builder().id(rs.getLong("id"))
				.name(rs.getString("name"))
				.build();
	}
}
