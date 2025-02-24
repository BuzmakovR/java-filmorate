package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;
import java.util.Optional;

@Repository("mpaRatingDbStorage")
public class MpaRatingDbStorage extends BaseRepository<MpaRating> implements MpaRatingStorage {

	private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";
	private static final String INSERT_QUERY = "INSERT INTO mpa_ratings(name) VALUES (?)";
	private static final String DELETE_QUERY = "DELETE FROM mpa_ratings WHERE id = ?";

	public MpaRatingDbStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Collection<MpaRating> getAll() {
		return findMany(FIND_ALL_QUERY);
	}

	@Override
	public MpaRating get(Long id) {
		Optional<MpaRating> optionalUser = findOne(FIND_BY_ID_QUERY, id);
		if (optionalUser.isEmpty()) throw new NotFoundException("Рейтинг с id = " + id + " не найден");
		return optionalUser.get();
	}

	@Override
	public MpaRating add(MpaRating rating) {
		long id = insert(
				INSERT_QUERY,
				rating.getName()
		);
		rating.setId(id);
		return rating;
	}

	@Override
	public MpaRating delete(Long id) {
		MpaRating rating = get(id);
		if (!delete(DELETE_QUERY, id)) {
			throw new InternalServerException("Не удалось удалить Рейтинг");
		}
		return rating;
	}
}
