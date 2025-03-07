package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmLikeRowMapper implements RowMapper<FilmLike> {

	@Override
	public FilmLike mapRow(ResultSet rs, int rowNum) throws SQLException {
		return FilmLike.builder()
				.filmId(rs.getLong("film_id"))
				.userId(rs.getLong("user_id"))
				.build();
	}
}
