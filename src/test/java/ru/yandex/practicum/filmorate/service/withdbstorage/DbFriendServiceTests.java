package ru.yandex.practicum.filmorate.service.withdbstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.FriendServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.repositories.FriendRequestDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.FriendRequestRowMapper;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendService.class, FriendRequestDbStorage.class, FriendRequestRowMapper.class,
		UserDbStorage.class, UserRowMapper.class})
public class DbFriendServiceTests extends FriendServiceTests {
}
