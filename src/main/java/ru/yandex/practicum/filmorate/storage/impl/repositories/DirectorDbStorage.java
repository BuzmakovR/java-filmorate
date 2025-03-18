package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.DirectorRowMapper;

import java.util.List;

@Slf4j
@Repository("directorDbStorage")
public class DirectorDbStorage extends BaseRepository<Director> implements DirectorStorage {

	private static final String SELECT_DIRECTOR_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
	private static final String SELECT_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
	private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO directors (name) VALUES (?)";
	private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
	private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = ?";

	public DirectorDbStorage(JdbcTemplate jdbc, DirectorRowMapper mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Director get(Long id) {
		return findOne(SELECT_DIRECTOR_BY_ID_QUERY, id)
				.orElseThrow(() -> new NotFoundException("Режиссер с ID " + id + " не найден"));
	}

	@Override
	public List<Director> getAll() {
		return findMany(SELECT_ALL_DIRECTORS_QUERY);
	}

	@Override
	public Director create(Director director) {
		long id = insert(INSERT_DIRECTOR_QUERY, director.getName());
		director.setId(id);
		log.info("Создан новый режиссёр с id: {}", id);
		return director;
	}

	@Override
	public Director update(Director newDirector) {
		update(UPDATE_DIRECTOR_QUERY, newDirector.getName(), newDirector.getId());
		log.info("Обновлён режиссёр с ID: {}", newDirector.getId());
		return newDirector;
	}

	@Override
	public void deleteById(Long id) {
		if (!delete(DELETE_DIRECTOR_QUERY, id)) {
			log.warn("Режиссёр с ID {} не найден, удаление не выполнено", id);
			throw new NotFoundException("Режиссёр с ID " + id + " не найден");
		}
		log.info("Удалён режиссёр с ID: {}", id);
	}
}
