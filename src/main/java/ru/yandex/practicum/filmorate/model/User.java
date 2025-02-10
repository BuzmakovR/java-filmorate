package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

	private Long id;

	@Email
	@NotBlank(message = "Email пользователя должен быть заполнен")
	private String email;

	@NotBlank(message = "Логин пользователя должен быть заполнен")
	private String login;

	private String name;

	@PastOrPresent(message = "Дата рождения пользователя не может быть в будущем")
	private LocalDate birthday;

	private final Set<Long> friendIds = new HashSet<>();

	public void validate() {
		if (getLogin() == null || getLogin().isBlank() || getLogin().contains(" ")) {
			throw new ValidationException("Логин пользователя не может быть пустым и содержать пробелы");
		}
	}

	public Set<Long> getFriendIds() {
		return Set.copyOf(friendIds);
	}

	public void addFriend(final Long friendId) {
		friendIds.add(friendId);
	}

	public void deleteFriend(final Long friendId) {
		friendIds.remove(friendId);
	}
}
