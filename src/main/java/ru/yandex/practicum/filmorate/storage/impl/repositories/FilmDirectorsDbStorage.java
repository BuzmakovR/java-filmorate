package ru.yandex.practicum.filmorate.storage.impl.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDirectorsStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository("filmDirectorsDbStorage")
public class FilmDirectorsDbStorage extends BaseRepository<Object> implements FilmDirectorsStorage {

	private static final String INSERT_FILM_DIRECTOR_QUERY =
			"INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
	private static final String DELETE_FILM_DIRECTORS_QUERY =
			"DELETE FROM film_directors WHERE film_id = ?";

	public FilmDirectorsDbStorage(JdbcTemplate jdbc) {
		super(jdbc, null);
	}

	@Override
	public void addFilmDirectors(Film film) {
		Set<Director> directors = film.getDirectors();
		if (directors == null || directors.isEmpty()) {
			log.info("Для фильма с ID {} не указаны режиссёры, добавление пропущено", film.getId());
			return;
		}
		List<Object[]> batchParams = directors.stream()
				.map(director -> new Object[]{film.getId(), director.getId()})
				.collect(Collectors.toList());
		jdbc.batchUpdate(INSERT_FILM_DIRECTOR_QUERY, batchParams);
		log.info("Добавлены {} режиссёров для фильма с ID: {}", batchParams.size(), film.getId());
	}

	@Override
	public void updateFilmDirectors(Film film) {
		if (film.getId() == null) {
			log.warn("Попытка обновить режиссёров для фильма без ID");
			throw new IllegalArgumentException("ID фильма должен быть указан");
		}
		boolean deleted = delete(DELETE_FILM_DIRECTORS_QUERY, film.getId());
		log.info("Режиссёры для фильма с ID: {} удалены успешно: {}", film.getId(), deleted);
		addFilmDirectors(film);
	}

}
