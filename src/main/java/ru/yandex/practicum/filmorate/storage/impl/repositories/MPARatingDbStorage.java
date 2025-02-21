package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.MPARatingStorage;

import java.util.Collection;
import java.util.Optional;

@Repository("mpaRatingDbStorage")
public class MPARatingDbStorage extends BaseRepository<MPARating> implements MPARatingStorage {

	private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";
	private static final String INSERT_QUERY = "INSERT INTO mpa_ratings(name) VALUES (?)";
	private static final String DELETE_QUERY = "DELETE FROM mpa_ratings WHERE id = ?";

	public MPARatingDbStorage(JdbcTemplate jdbc, RowMapper<MPARating> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Collection<MPARating> getAll() {
		return findMany(FIND_ALL_QUERY);
	}

	@Override
	public MPARating get(Long id) {
		Optional<MPARating> optionalUser = findOne(FIND_BY_ID_QUERY, id);
		if (optionalUser.isEmpty()) throw new NotFoundException("Рейтинг с id = " + id + " не найден");
		return optionalUser.get();
	}

	@Override
	public MPARating add(MPARating rating) {
		long id = insert(
				INSERT_QUERY,
				rating.getName()
		);
		rating.setId(id);
		return rating;
	}

	@Override
	public MPARating delete(Long id) {
		MPARating rating = get(id);
		if (!delete(DELETE_QUERY, id)) {
			throw new InternalServerException("Не удалось удалить Рейтинг");
		}
		return rating;
	}
}
