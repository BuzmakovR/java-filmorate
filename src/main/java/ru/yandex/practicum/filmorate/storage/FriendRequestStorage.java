package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface FriendRequestStorage {

	Collection<Long> getUserFriendIds(Long id);

	void addUserFriend(Long userId, Long friendId);

	void deleteUserFriend(Long userId, Long friendId);

	void deleteAllFriendsRequestForUser(Long userId);

	Collection<Long> getCommonFriendIds(Long userId1, Long userId2);
}
