package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedResource.EventOperation;
import ru.yandex.practicum.filmorate.model.feedResource.EventType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("inMemoryFeedStorage")
public class InMemoryFeedStorage implements FeedStorage {

    Map<Long, Feed> storage = new HashMap<>();

    @Override
    public void addEvent(Long userId, Long entityId, EventOperation eventOperation, EventType eventType) {
        storage.put(getNextId(),
                Feed.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .eventOperation(eventOperation)
                        .eventType(eventType)
                        .eventId(getNextId())
                        .userId(userId)
                        .entityId(entityId)
                        .build());
    }

    @Override
    public Collection<Feed> getFeed(Long userId) {
        return List.copyOf(storage.values());
    }

    private long getNextId() {
        long currentMaxId = storage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
