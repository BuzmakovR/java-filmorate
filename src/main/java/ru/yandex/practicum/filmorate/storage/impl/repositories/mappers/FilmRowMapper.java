package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class FilmRowMapper implements RowMapper<Film> {

	@Override
	public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
		Film film = Film.builder()
				.id(rs.getLong("id"))
				.name(rs.getString("name"))
				.description(rs.getString("description"))
				.duration(rs.getInt("duration"))
				.build();

		Long mpaId = rs.getLong("mpa_rating_id");
		String mpaName = rs.getString("mpa_rating_name");
		if (!mpaName.isBlank()) {
			film.setMpa(MPARating.builder().id(mpaId).name(mpaName).build());
		}

		Optional.ofNullable(rs.getDate("release_date"))
				.ifPresent(date -> film.setReleaseDate(date.toLocalDate()));

		List<String> genreIds = Optional.ofNullable(rs.getString("genre_id"))
				.map(s -> List.of(s.split(",")))
				.orElse(List.of());

		List<String> genreNames = Optional.ofNullable(rs.getString("genre_name"))
				.map(s -> List.of(s.split(",")))
				.orElse(List.of());

		for (int i = 0; i < genreIds.size(); i++) {
			if (i >= genreNames.size()) break;
			film.getGenres().add(Genre.builder().id(Long.valueOf(genreIds.get(i)))
					.name(genreNames.get(i)).build());
		}
		return film;
	}
}
