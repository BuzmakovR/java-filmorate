package ru.yandex.practicum.filmorate.service.withdbstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.withinmemory.InMemoryReviewServiceTests;
import ru.yandex.practicum.filmorate.storage.impl.repositories.*;
import ru.yandex.practicum.filmorate.storage.impl.repositories.mappers.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewService.class,
		ReviewDbStorage.class, ReviewRowMapper.class,
		ReviewLikeDbStorage.class, ReviewLikeRowMapper.class,
		FilmDbStorage.class, FilmRowMapper.class,
		FilmLikeDbStorage.class, FilmLikeRowMapper.class,
		UserDbStorage.class, UserRowMapper.class,
		FeedDbStorage.class, FeedRowMapper.class,
		FilmDirectorsDbStorage.class
})
public class DbReviewServiceTests extends InMemoryReviewServiceTests {
}
