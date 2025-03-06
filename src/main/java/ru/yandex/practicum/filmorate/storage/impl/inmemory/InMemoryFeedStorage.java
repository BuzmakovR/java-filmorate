package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.Collection;
import java.util.List;

@Component("inMemoryFeedStorage")
public class InMemoryFeedStorage implements FeedStorage {
	@Override
	public void addEvent(Long userId, Long entityId, EventOperation eventOperation, EventType eventType) {

	}

	@Override
	public Collection<Feed> getFeed(Long userId) {
		return List.of();
	}
}
