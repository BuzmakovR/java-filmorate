package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository("genreDbStorage")
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

	private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
	private static final String INSERT_QUERY = "INSERT INTO genres(name) VALUES (?)";
	private static final String DELETE_QUERY = "DELETE FROM genres WHERE id = ?";

	public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Collection<Genre> getAll() {
		return findMany(FIND_ALL_QUERY);
	}

	@Override
	public Genre get(Long id) {
		Optional<Genre> optionalUser = findOne(FIND_BY_ID_QUERY, id);
		if (optionalUser.isEmpty()) throw new NotFoundException("Жанр с id = " + id + " не найден");
		return optionalUser.get();
	}

	@Override
	public Genre add(Genre genre) {
		long id = insert(
				INSERT_QUERY,
				genre.getName()
		);
		genre.setId(id);
		return genre;
	}

	@Override
	public Genre delete(Long id) {
		Genre genre = get(id);
		if (!delete(DELETE_QUERY, id)) {
			throw new InternalServerException("Не удалось удалить жанр");
		}
		return genre;
	}
}
