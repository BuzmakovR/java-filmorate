package ru.yandex.practicum.filmorate.service.withdbstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.repositories.*;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmService.class, FilmDbStorage.class, FilmRowMapper.class,
		UserDbStorage.class, UserRowMapper.class,
		FilmLikeDbStorage.class, FilmLikeRowMapper.class,
		GenreDbStorage.class, GenreRowMapper.class,
		MpaRatingDbStorage.class, MpaRatingRowMapper.class
})
public class DbFilmServiceTests extends FilmServiceTests {
}
