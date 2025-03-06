package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
	@Override
	public Feed mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Feed feed = new Feed();
		feed.setEventId(resultSet.getLong("event_id"));
		feed.setUserId(resultSet.getLong("user_id"));
		feed.setEntityId(resultSet.getLong("entity_id"));
		feed.setEventType(EventType.valueOf(resultSet.getString("event_type")));
		feed.setEventOperation(EventOperation.valueOf(resultSet.getString("event_operation")));
		feed.setTimestamp(resultSet.getLong("timestamp"));
		return feed;
	}
}