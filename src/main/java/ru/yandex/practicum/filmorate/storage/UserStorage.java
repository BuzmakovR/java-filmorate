package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

	Collection<User> getAll();

	Optional<User> get(Long id);

	User add(User user);

	User update(User newUser);

	User delete(Long id);
}
