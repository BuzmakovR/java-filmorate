package ru.yandex.practicum.filmorate.storage.impl.repositories.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = User.builder()
				.id(rs.getLong("id"))
				.email(rs.getString("email"))
				.login(rs.getString("login"))
				.name(rs.getString("name"))
				.build();

		Optional.ofNullable(rs.getDate("birthday"))
				.ifPresent(date -> user.setBirthday(date.toLocalDate()));
		return user;
	}
}
