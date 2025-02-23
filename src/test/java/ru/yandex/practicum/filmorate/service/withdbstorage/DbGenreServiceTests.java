package ru.yandex.practicum.filmorate.service.withdbstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.GenreServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.repositories.*;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreService.class, GenreDbStorage.class, GenreRowMapper.class})
public class DbGenreServiceTests extends GenreServiceTests {
}
