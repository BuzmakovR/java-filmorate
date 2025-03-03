package ru.yandex.practicum.filmorate.storage.impl.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Repository("userDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

	private static final String FIND_ALL_QUERY = "SELECT * FROM users";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
	private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";
	private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";

	public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
		super(jdbc, mapper);
	}

	@Override
	public Collection<User> getAll() {
		return findMany(FIND_ALL_QUERY);
	}

	@Override
	public User get(Long id) {
		Optional<User> optionalUser = findOne(FIND_BY_ID_QUERY, id);
		if (optionalUser.isEmpty()) throw new NotFoundException("Пользователь с id = " + id + " не найден");
		return optionalUser.get();
	}

	@Override
	public User add(User user) {
		long id = insert(
				INSERT_QUERY,
				user.getEmail(),
				user.getLogin(),
				user.getName(),
				user.getBirthday()
		);
		user.setId(id);
		return user;
	}

	@Override
	public User update(User newUser) {
		update(
				UPDATE_QUERY,
				newUser.getLogin(),
				newUser.getEmail(),
				newUser.getName(),
				newUser.getBirthday(),
				newUser.getId()
		);
		return newUser;
	}

	@Override
	public User delete(Long id) {
		User user = get(id);
		if (!delete(DELETE_QUERY, id)) {
			throw new InternalServerException("Не удалось удалить пользователя");
		}
		return user;
	}

}
