package ru.yandex.practicum.filmorate.service.withdbstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.service.MPARatingService;
import ru.yandex.practicum.filmorate.service.MPARatingServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.repositories.MPARatingDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.MPARatingRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MPARatingService.class, MPARatingDbStorage.class, MPARatingRowMapper.class})
public class DbMPARatingServiceTests extends MPARatingServiceTests {
}
