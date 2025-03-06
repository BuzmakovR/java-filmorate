package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.time.Instant;
import java.util.Collection;

@Repository("feedDbStorage")
public class FeedDbStorage extends BaseRepository<Feed> implements FeedStorage {

	private static final String INSERT_QUERY = """
			INSERT INTO feeds (user_id, entity_id, timestamp, event_type, event_operation)
			VALUES (?, ?, ?, ?, ?)""";
	private static final String FIND_BY_USERID_QUERY = "SELECT * FROM feeds WHERE user_id = ? ORDER BY event_id DESC";

	public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public void addEvent(Long userId, Long entityId, EventOperation eventOperation, EventType eventType) {
		insert(INSERT_QUERY, userId, entityId, Instant.now().toEpochMilli(), eventType.name(), eventOperation.name());
	}

	@Override
	public Collection<Feed> getFeed(Long userId) {
		return findMany(FIND_BY_USERID_QUERY, userId);
	}
}
