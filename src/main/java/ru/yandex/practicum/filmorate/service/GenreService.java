package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {

	@Autowired
	@Qualifier("genreDbStorage")
	private final GenreStorage genreStorage;

	@Autowired
	public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
		this.genreStorage = genreStorage;
	}

	public Collection<Genre> getGenres() {
		return genreStorage.getAll();
	}

	public Genre getGenre(final Long genreId) {
		return genreStorage.get(genreId);
	}

	public Genre addGenre(Genre genre) {
		return genreStorage.add(genre);
	}

	public Genre deleteGenre(final Long genreId) {
		return genreStorage.delete(genreId);
	}
}
