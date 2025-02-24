package ru.yandex.practicum.filmorate.service.withdbstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.service.MpaRatingServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.repositories.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.MpaRatingRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRatingService.class, MpaRatingDbStorage.class, MpaRatingRowMapper.class})
public class DbMpaRatingServiceTests extends MpaRatingServiceTests {
}
