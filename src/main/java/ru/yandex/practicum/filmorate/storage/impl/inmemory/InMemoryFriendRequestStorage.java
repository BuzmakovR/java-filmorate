package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.storage.FriendRequestStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;

@Component("inMemoryFriendRequestStorage")
@RequiredArgsConstructor
public class InMemoryFriendRequestStorage implements FriendRequestStorage {

	private final Set<FriendRequest> friends = new HashSet<>();

	@Override
	public Collection<Long> getUserFriendIds(Long id) {
		return friends.stream()
				.filter(friendRequest -> Objects.equals(friendRequest.getUserId(), id))
				.map(FriendRequest::getFriendId)
				.toList();
	}

	@Override
	public void addUserFriend(Long userId, Long friendId) {
		Optional<FriendRequest> reversRequestOptional = friends.stream()
				.filter(friendRequest -> Objects.equals(friendRequest.getUserId(), friendId))
				.findFirst();

		FriendRequest newRequest = FriendRequest.builder()
				.userId(userId)
				.friendId(friendId)
				.build();

		if (reversRequestOptional.isPresent()) {
			FriendRequest reversRequest = reversRequestOptional.get();
			newRequest.setConfirmed(true);
			reversRequest.setConfirmed(true);
			friends.add(reversRequest);
		}
		friends.add(newRequest);
	}

	@Override
	public void deleteUserFriend(Long userId, Long friendId) {
		friends.stream()
				.filter(friendRequest -> Objects.equals(friendRequest.getUserId(), userId))
				.findFirst()
				.ifPresent(friends::remove);

		friends.stream()
				.filter(friendRequest -> Objects.equals(friendRequest.getUserId(), friendId))
				.findFirst()
				.ifPresent(friendRequest -> {
					friendRequest.setConfirmed(false);
					friends.add(friendRequest);
				});
	}

	@Override
	public Collection<Long> getCommonFriendIds(Long userId1, Long userId2) {
		Collection<Long> friend1 = friends.stream()
				.filter(fr -> Objects.equals(fr.getUserId(), userId1))
				.map(FriendRequest::getFriendId)
				.toList();

		return friends.stream()
				.filter(fr -> Objects.equals(fr.getUserId(), userId2) && friend1.contains(fr.getFriendId()))
				.map(FriendRequest::getFriendId)
				.toList();
	}
}
