package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRequestRowMapper implements RowMapper<FriendRequest> {

	@Override
	public FriendRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
		return FriendRequest.builder()
				.userId(rs.getLong("user_id"))
				.friendId(rs.getLong("friend_id"))
				.isConfirmed(rs.getBoolean("is_confirmed"))
				.build();
	}
}
