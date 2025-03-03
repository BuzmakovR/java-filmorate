package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.storage.FriendRequestStorage;

import java.util.Collection;

@Repository("friendRequestDbStorage")
public class FriendRequestDbStorage extends BaseRepository<FriendRequest> implements FriendRequestStorage {

	private static final String FIND_FRIENDS_BY_ID = "SELECT * FROM friend_requests WHERE user_id = ?";
	private static final String DELETE_FRIEND_BY_ID = "DELETE FROM friend_requests WHERE user_id = ? AND friend_id = ?";
	private static final String INSERT_QUERY = "INSERT INTO friend_requests(user_id, friend_id, is_confirmed) " +
			"SELECT ?, ?, " +
			"CASE " +
			"WHEN (SELECT count(1) FROM friend_requests WHERE friend_id = ? AND user_id = ?) = 1 THEN TRUE " +
			"ELSE FALSE  " +
			"END " +
			"FROM dual";
	private static final String UPDATE_QUERY = "UPDATE friend_requests SET is_confirmed = " +
			"CASE " +
			"WHEN (SELECT count(1) FROM friend_requests WHERE friend_id = ? AND user_id = ?) = 1 THEN TRUE " +
			"ELSE FALSE  " +
			"END " +
			"WHERE user_id = ? AND friend_id = ?";
	private static final String GET_COMMON_FRIENDS = "SELECT u1.user_id as user_id, u1.friend_id as friend_id, u1.is_confirmed as is_confirmed " +
			"FROM friend_requests u1 " +
			"JOIN friend_requests u2 ON u1.friend_id = u2.friend_id " +
			"where u1.user_id = ? " +
			"AND u2.user_id = ? ";

	public FriendRequestDbStorage(JdbcTemplate jdbc, RowMapper<FriendRequest> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Collection<Long> getUserFriendIds(Long id) {
		return findMany(FIND_FRIENDS_BY_ID, id).stream()
				.map(FriendRequest::getFriendId)
				.toList();
	}

	@Override
	public void addUserFriend(Long userId, Long friendId) {
		update(
				INSERT_QUERY,
				userId,
				friendId,
				friendId,
				userId
		);
		updateWithoutCheck(
				UPDATE_QUERY,
				friendId,
				userId,
				userId,
				friendId
		);
	}

	@Override
	public void deleteUserFriend(Long userId, Long friendId) {
		delete(
				DELETE_FRIEND_BY_ID,
				userId,
				friendId
		);
		updateWithoutCheck(
				UPDATE_QUERY,
				userId,
				friendId,
				friendId,
				userId
		);
	}

	@Override
	public Collection<Long> getCommonFriendIds(Long userId1, Long userId2) {
		return findMany(GET_COMMON_FRIENDS, userId1, userId2).stream()
				.map(FriendRequest::getFriendId)
				.toList();
	}
}
